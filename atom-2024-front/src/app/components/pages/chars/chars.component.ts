import {Component, OnInit} from '@angular/core';
import {ChartModule} from "primeng/chart";
import {ButtonModule} from "primeng/button";
import {TooltipModule} from "primeng/tooltip";
import html2canvas from "html2canvas";

@Component({
  selector: 'app-chars',
  standalone: true,
  imports: [
    ChartModule,
    ButtonModule,
    TooltipModule
  ],
  templateUrl: './chars.component.html',
  styleUrl: './chars.component.css'
})
export class CharsComponent implements OnInit {
  basicData: any;

  basicOptions: any;

  ngOnInit() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.basicData = {
      labels: ['Q1', 'Q2', 'Q3', 'Q4'],
      datasets: [
        {
          label: 'Sales',
          data: [540, 325, 702, 620],
          backgroundColor: ['rgba(255, 159, 64, 0.2)', 'rgba(75, 192, 192, 0.2)', 'rgba(54, 162, 235, 0.2)', 'rgba(153, 102, 255, 0.2)'],
          borderColor: ['rgb(255, 159, 64)', 'rgb(75, 192, 192)', 'rgb(54, 162, 235)', 'rgb(153, 102, 255)'],
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
}
