import {Component, HostListener, OnInit} from '@angular/core';
import {TooltipModule} from 'primeng/tooltip';
import {NgForOf} from '@angular/common';
import {ImageModule} from 'primeng/image';

export enum FeedbackType {
  SUCCESS = 'SUCCESS', WARN = 'WARN', ERROR = 'ERROR'
}

@Component({
  selector: 'app-image-with-feedback-viewer',
  standalone: true,
  imports: [
    TooltipModule,
    NgForOf,
    ImageModule
  ],
  templateUrl: './image-with-feedback-viewer.component.html',
  styleUrl: './image-with-feedback-viewer.component.css'
})
export class ImageWithFeedbackViewerComponent implements OnInit {
  scaleCoefficient = 1;
  imgSource: HTMLImageElement;

  feedbacks = [
    {
      x1: 1,
      y1: 5,
      x2: 50,
      y2: 40,
      type: FeedbackType.SUCCESS,
      style: {}
    },
    {
      x1: 60,
      y1: 90,
      x2: 100,
      y2: 150,
      type: FeedbackType.WARN,
      style: {}
    },
    {
      x1: 200,
      y1: 200,
      x2: 300,
      y2: 300,
      type: FeedbackType.ERROR,
      style: {}
    }
  ];

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.calculateStyles();
  }

  protected readonly FeedbackType = FeedbackType;

  constructor() {
  }

  ngOnInit(): void {
  }

  onImageLoad(imgSource: any) {
    this.imgSource = imgSource;
    this.calculateStyles();
  }

  calculateStyles() {
    this.scaleCoefficient = this.imgSource.clientWidth / this.imgSource.naturalWidth;

    this.feedbacks.forEach(f => {
      f.style = {
        left: this.getScaledPx(f.x1),
        top: this.getScaledPx(f.y2),
        width: this.getScaledPx(f.x2 - f.x1),
        height: this.getScaledPx(f.y2 - f.y1)
      }
    })
  }

  getScaledPx(origCoordinate: number): string {
    return origCoordinate * this.scaleCoefficient + 'px';
  }
}
