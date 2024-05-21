import {Table} from "primeng/table";
import {Column} from "./Column";
import * as FileSaver from 'file-saver';
import {MenuItem, MessageService} from "primeng/api";
import autoTable from "jspdf-autotable";
import {BehaviorSubject} from "rxjs";

export class ExportTable {
  static messageSerivce: MessageService;

  static exportMenu: BehaviorSubject<MenuItem[]> = new BehaviorSubject(
    [] as MenuItem[]
  );

  static showMenu(table: Table, columns: Column[], data: any[], fileName?: string): void {
    this.exportMenu.next([
      {
        label: 'Выберите формат',
        items: fileName ? [
          {
            label: 'EXCEL',
            icon: 'pi pi-file-excel',
            command: () => ExportTable.exportExcel(columns, data, fileName)
          },
          {
            label: 'PDF',
            icon: 'pi pi-file-pdf',
            command: () => ExportTable.exportPdf(columns, data, fileName)
          }
        ] : [
          {
            label: 'CSV',
            icon: 'pi pi-file',
            command: () => table.exportCSV()
          },
          {
            label: 'EXCEL',
            icon: 'pi pi-file-excel',
            command: () => ExportTable.exportExcel(columns, data)
          },
          {
            label: 'PDF',
            icon: 'pi pi-file-pdf',
            command: () => ExportTable.exportPdf(columns, data)
          }
        ]
      }
    ]);
  }

  static exportPdf(columns: Column[], data: any[], fileName?: string) {
    if (this.validateExport(columns, data)) {
      const headers = columns.map((col) => col.header);
      const body: any[][] = [];
      data.forEach(row => {
        const pdfRow: any[] = [];
        columns.forEach(col => {
          pdfRow.push(row[col.fieldGetter ?? col.field])
        })
        body.push(pdfRow);
      })

      import('jspdf').then((jsPDF) => {
        import('jspdf-autotable').then((x) => {
          const doc = new jsPDF.default('landscape', 'px', 'a4');

          function getBase64(file: Blob) {
            return new Promise((resolve, reject) => {
              const reader = new FileReader();
              reader.readAsDataURL(file);
              reader.onload = () => resolve(reader.result);
              reader.onerror = error => reject(error);
            });
          }

          let pFont = fetch("/assets/fonts/Inter-Regular.ttf")
            .then(response => response.blob())
            .then(response => getBase64(response))
            .then(response => (response as string).split(',')[1])
            .then(fontBase64 => {
              doc.addFileToVFS('Inter-Regular.ttf', fontBase64);
              doc.addFont('Inter-Regular.ttf', 'Inter', 'normal');
              autoTable(doc, {
                head: [headers],
                body: body,
                styles: {
                  font: 'Inter'
                }
              })

              doc.save(`${fileName ?? 'download'}.pdf`);
            })
        });
      });
    }
  }

  static exportExcel(columns: Column[], data: any[], fileName?: string) {
    if (this.validateExport(columns, data)) {
      const exportData: any[] = [];
      data.forEach(row => {
        const excelRow: any = {};
        columns.forEach(col => {
          excelRow[col.header] = row[col.fieldGetter ?? col.field];
        })
        exportData.push(excelRow);
      })

      import('xlsx').then((xlsx) => {
        let wb = xlsx.utils.book_new();
        let ws = xlsx.utils.json_to_sheet(exportData);
        ws['!cols'] = [];

        const widths: { wch: number }[] = [];
        Object.keys(exportData[0]).forEach(key => {
          widths.push({wch: Math.max(...exportData.map(d => d[key] ? d[key].toString().length + 2 : 6))})
        });

        ws['!cols'] = widths;

        xlsx.utils.book_append_sheet(wb, ws, 'Данные');
        const excelBuffer: any = xlsx.write(wb, {bookType: 'xlsx', type: 'array'});
        this.saveAsExcelFile(excelBuffer, `${fileName ?? 'download'}`);
      });
    }
  }

  static saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE
    });
    FileSaver.saveAs(data, fileName + EXCEL_EXTENSION);
  }

  static validateExport(columns: Column[], data: any[]): boolean {
    if (!columns || !data) {
      this.messageSerivce.add({severity: 'warn', summary: 'Внимание', detail: 'Экспорт пустой таблицы невозможен'});
      return false;
    }
    return true;
  }
}
