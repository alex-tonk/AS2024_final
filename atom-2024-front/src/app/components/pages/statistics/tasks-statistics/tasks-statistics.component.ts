import {Component, OnInit} from '@angular/core';
import {Button} from 'primeng/button';
import {ChartModule} from 'primeng/chart';
import {TabViewModule} from 'primeng/tabview';
import {TaskService} from '../../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import html2canvas from 'html2canvas';
import {TaskDto} from '../../../../gen/atom2024backend-dto';
import {getField} from '../../../../services/field-accessor';
import {CheckboxModule} from 'primeng/checkbox';
import {
  ColumnFilterWrapperComponent
} from '../../../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {InputTextModule} from 'primeng/inputtext';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {TableModule} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {ExportTable} from '../../../common/table/ExportTable';
import {MenuModule} from 'primeng/menu';
import {Column} from '../../../common/table/Column';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-tasks-statistics',
  standalone: true,
  imports: [
    Button,
    ChartModule,
    TabViewModule,
    CheckboxModule,
    ColumnFilterWrapperComponent,
    InputTextModule,
    NgForOf,
    NgIf,
    TableModule,
    TooltipModule,
    AsyncPipe,
    MenuModule,
    FormsModule
  ],
  templateUrl: './tasks-statistics.component.html',
  styleUrl: './tasks-statistics.component.css'
})
export class TasksStatisticsComponent implements OnInit {
  protected readonly getField = getField;

  loading = false;
  filter = false;

  tasks: TaskDto[] = [];

  basicData: any;
  basicOptions: any;

  columns: Column[] = [{
    header: 'ID',
    field: 'id',
    type: 'numeric',
    width: 10
  }, {
    header: 'Заголовок',
    field: 'title',
    width: 25
  }, {
    header: 'Код',
    field: 'code'
  }, {
    header: 'Сложность',
    field: 'difficulty'
  }, {
    header: 'Сложность (фактическая)',
    field: 'difficultyScore'
  }];

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(private taskService: TaskService) {

  }

  async ngOnInit() {
    this.tasks = (await lastValueFrom(this.taskService.getTasksWithStats())).sort((a, b) => b.difficulty! - a.difficulty!);

    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.basicData = {
      labels: this.tasks.map(t => t.code),
      datasets: [
        {
          label: 'Сложность',
          data: this.tasks.map(t => t.difficulty),
          backgroundColor: ['rgba(255, 159, 64, 0.2)', 'rgba(75, 192, 192, 0.2)', 'rgba(54, 162, 235, 0.2)', 'rgba(153, 102, 255, 0.2)'],
          borderColor: ['rgb(255, 159, 64)', 'rgb(75, 192, 192)', 'rgb(54, 162, 235)', 'rgb(153, 102, 255)'],
          borderWidth: 1
        },
        {
          label: 'Сложность (фактическая)',
          data: this.tasks.map(t => t.difficultyScore),
          backgroundColor: ['rgba(255, 159, 64, 0.2)', 'rgba(75, 192, 192, 0.2)', 'rgba(54, 162, 235, 0.2)', 'rgba(153, 102, 255, 0.2)'].reverse(),
          borderColor: ['rgb(255, 159, 64)', 'rgb(75, 192, 192)', 'rgb(54, 162, 235)', 'rgb(153, 102, 255)'].reverse(),
          borderWidth: 1
        }
      ]
    };

    this.basicOptions = {
      plugins: {
        legend: {
          labels: {
            color: textColor
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        },
        x: {
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        }
      }
    };
  }

  print() {
    import('jspdf').then((jsPDF) => {
      const printContent = document.getElementById('printArea');
      if (printContent) {
        html2canvas(printContent).then(canvas => {
          const doc = new jsPDF.default('portrait', 'px', 'a4');
          const imgData = canvas.toDataURL();

          /*
          const imgProps= doc.getImageProperties(imgData);

          FOR landscape
          onst pdfWidth = doc.internal.pageSize.getWidth();
          const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;

          FOR portrait
          const pdfHeight = doc.internal.pageSize.getHeight();
          const pdfWidth = (imgProps.width * pdfHeight) / imgProps.height;
          */

          /*doc.addImage(imgData, 0, 0, printContent.clientWidth, printContent.clientHeight);
          doc.save('export.pdf');*/

          const printWindow: Window = window.open()!;
          printWindow.document.write('<html><head><title>На печать</title>');
          printWindow.document.write('</head><body >');
          printWindow.document.write(`<img src="${imgData}">`);
          printWindow.document.write('</body></html>');
          printWindow.document.close();
          setTimeout(() => {
            printWindow.print();
            printWindow.close();
          }, 1000)
        })
      }
    });
  }

  protected readonly ExportTable = ExportTable;
}
