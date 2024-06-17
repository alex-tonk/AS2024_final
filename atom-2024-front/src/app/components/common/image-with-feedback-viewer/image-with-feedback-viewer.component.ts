import {Component, HostListener, Input, OnInit, QueryList, ViewChildren} from '@angular/core';
import {TooltipModule} from 'primeng/tooltip';
import {NgForOf, NgIf} from '@angular/common';
import {ImageModule} from 'primeng/image';
import {Button} from 'primeng/button';
import {DropdownModule} from 'primeng/dropdown';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {InputTextModule} from 'primeng/inputtext';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {SplitterModule} from 'primeng/splitter';

export enum FeedbackType {
  COMMENT = 'COMMENT', WARN = 'WARN', ERROR = 'ERROR'
}

@Component({
  selector: 'app-image-with-feedback-viewer',
  standalone: true,
  imports: [
    TooltipModule,
    NgForOf,
    ImageModule,
    NgIf,
    Button,
    DropdownModule,
    FormsModule,
    OverlayPanelModule,
    ReactiveFormsModule,
    InputTextModule,
    InputTextareaModule,
    SplitterModule
  ],
  templateUrl: './image-with-feedback-viewer.component.html',
  styleUrl: './image-with-feedback-viewer.component.css'
})
export class ImageWithFeedbackViewerComponent implements OnInit {
  @Input() isTutorMode = true;
  @ViewChildren('op') overlayPanels: QueryList<OverlayPanelModule>;
  creatingFeedbackType: FeedbackType;

  scaleCoefficient = 1;
  imgSource: HTMLImageElement;

  tutorSelectionMode = false;
  tutorSelectionDiv: HTMLElement;
  x1 = 0;
  y1 = 0;
  x2 = 0;
  y2 = 0;

  lastSelectionCoords: { x1: number, y1: number, x2: number, y2: number };
  hoveredFeedback: any;

  feedbacks = [
    {
      x1: 1,
      y1: 5,
      x2: 50,
      y2: 40,
      type: FeedbackType.WARN,
      comment: 'Вау какой дурачок',
      isEditing: false,
      style: {}
    },
    {
      x1: 60,
      y1: 90,
      x2: 100,
      y2: 150,
      type: FeedbackType.WARN,
      comment: 'Блин реально какой-то дурачок',
      isEditing: false,
      style: {}
    },
    {
      x1: 200,
      y1: 200,
      x2: 300,
      y2: 300,
      type: FeedbackType.ERROR,
      comment: 'Вы знаете какой тут эмодзи',
      isEditing: false,
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

  addFeedback(type: FeedbackType) {
    this.creatingFeedbackType = type;
    this.tutorSelectionMode = true;
    this.tutorSelectionDiv = document.getElementById('tutorSelection')!;
  }

  onImageLoad(imgSource: any) {
    this.imgSource = imgSource;
    this.calculateStyles();
  }

  calculateStyles() {
    this.scaleCoefficient = this.imgSource.clientWidth / this.imgSource.naturalWidth;
    this.feedbacks.forEach(f => this.calculateStyle(f))
  }

  calculateStyle(feedback: any) {
    feedback.style = {
      left: this.getScaledPx(feedback.x1) + 'px',
      top: this.getScaledPx(feedback.y1) + 'px',
      width: this.getScaledPx(feedback.x2 - feedback.x1) + 'px',
      height: this.getScaledPx(feedback.y2 - feedback.y1) + 'px'
    }
  }

  getScaledPx(origCoordinate: number): number {
    return origCoordinate * this.scaleCoefficient;
  }

  getUnscaledPx(scaledCoordinate: number): number {
    return scaledCoordinate / this.scaleCoefficient;
  }

  reCalcTutorSelection() {
    const x3 = Math.min(this.x1, this.x2);
    const x4 = Math.max(this.x1, this.x2);
    const y3 = Math.min(this.y1, this.y2);
    const y4 = Math.max(this.y1, this.y2);
    this.tutorSelectionDiv.style.left = x3 + 'px';
    this.tutorSelectionDiv.style.top = y3 + 'px';
    this.tutorSelectionDiv.style.width = x4 - x3 + 'px';
    this.tutorSelectionDiv.style.height = y4 - y3 + 'px';

    this.lastSelectionCoords = {x1: x3, y1: y3, x2: x4, y2: y4};
  }

  onMouseDown(event: MouseEvent, imgWrapper: HTMLDivElement) {
    this.stopNativeEvent(event);
    if (!this.tutorSelectionMode) {
      return;
    }
    this.tutorSelectionDiv.hidden = false;
    this.x1 = event.clientX - imgWrapper.offsetLeft;
    this.y1 = event.clientY - imgWrapper.offsetTop;
    this.reCalcTutorSelection();
  }

  onMouseMove(event: MouseEvent, imgWrapper: HTMLDivElement) {
    if (!this.tutorSelectionMode) {
      return;
    }
    this.x2 = event.clientX - imgWrapper.offsetLeft;
    this.y2 = event.clientY - imgWrapper.offsetTop;
    this.reCalcTutorSelection();
  }

  onMouseUp() {
    if (!this.tutorSelectionMode) {
      return;
    }

    const newFeedback = {
      x1: this.getUnscaledPx(this.lastSelectionCoords.x1),
      y1: this.getUnscaledPx(this.lastSelectionCoords.y1),
      x2: this.getUnscaledPx(this.lastSelectionCoords.x2),
      y2: this.getUnscaledPx(this.lastSelectionCoords.y2),
      type: this.creatingFeedbackType,
      style: {},
      isEditing: true,
      comment: ''
    };
    this.calculateStyle(newFeedback);

    this.feedbacks.push(newFeedback);
    this.tutorSelectionDiv.hidden = true;
    this.tutorSelectionMode = false;
  }

  stopNativeEvent(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
  }

  onHoverFeedbackInList(feedback: any) {
    this.hoveredFeedback = feedback;
  }
}
