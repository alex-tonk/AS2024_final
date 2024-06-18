import {Component, ElementRef, HostListener, Input, OnInit, ViewChild} from '@angular/core';
import {TooltipModule} from 'primeng/tooltip';
import {NgForOf, NgIf, NgStyle} from '@angular/common';
import {ImageModule} from 'primeng/image';
import {Button} from 'primeng/button';
import {DropdownModule} from 'primeng/dropdown';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {InputTextModule} from 'primeng/inputtext';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {SplitterModule} from 'primeng/splitter';
import {AngularDraggableModule, IPosition} from 'angular2-draggable';
import {AttemptCheckResultDto, AttemptDto} from '../../../gen/atom2024backend-dto';
import {lastValueFrom} from 'rxjs';
import {AttemptService} from '../../../gen/atom2024backend-controllers';
import {MessageService} from 'primeng/api';
import {ConfigService} from '../../../services/config.service';

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
    SplitterModule,
    AngularDraggableModule,
    NgStyle
  ],
  templateUrl: './image-with-feedback-viewer.component.html',
  styleUrl: './image-with-feedback-viewer.component.css'
})
export class ImageWithFeedbackViewerComponent implements OnInit {
  @Input() isTutorMode = true;
  @Input() attemptId: number;
  attempt: AttemptDto;
  loading = false;
  baseUrl: string;

  @ViewChild('imgWrapper') imgWrapper: ElementRef;
  creatingFeedbackType: FeedbackType;

  scaleCoefficient = 1;
  imgSource: HTMLImageElement;

  isDrawingNow = false;
  tutorSelectionDiv: HTMLElement;

  startCoordinates = {x1: 0, y1: 0, x2: 0, y2: 0};
  lastCoordinates = {x1: 0, y1: 0, x2: 0, y2: 0};
  hoveredFeedback: any;

  fileIds = [102, 101];
  results: any [];
  resultsAIBackup: AttemptCheckResultDto[];


  // TODO Надо пофиксить ресайз, пока пофиг
  @HostListener('window:resize', ['$event'])
  onResize() {
    this.calculateStyles();
  }

  protected readonly FeedbackType = FeedbackType;

  constructor(
    private attemptService: AttemptService,
    private messageService: MessageService,
    private configService: ConfigService,
  ) {
    this.baseUrl = this.configService.baseUrl;
  }

  async ngOnInit() {
  }

  async onImageLoad(imgSource: any) {
    this.imgSource = imgSource;
    await this.init();
    this.calculateStyles();
    this.setInitialOffset();
  }

  async init() {
    this.loading = true;
    try {
      this.attempt = await lastValueFrom(this.attemptService.getAttempt(this.attemptId));
      if (!this.attempt) {
        this.messageService.add({severity: 'error', summary: 'Внимание', detail: 'Работа не найдена в БД'});
      } else {
        this.prepareData();
        if (this.attempt.autoCheckResults) {
          this.resultsAIBackup = this.attempt.autoCheckResults?.map(r => r);
        }
      }
    } finally {
      this.loading = false;
    }
  }

  prepareData() {
    this.results = [];
    if (this.attempt.autoStatus === 'В обработке') {
      this.messageService.add({severity: 'warn', summary: 'Внимание', detail: 'Система ИИ все еще проверяет работу'});
    }
    if (!this.attempt.autoCheckResults || this.attempt.autoCheckResults.length === 0) {
      this.messageService.add({severity: 'info', summary: 'Внимание', detail: 'Система ИИ не нашла ошибок в работе'});
    } else {
      this.attempt.autoCheckResults.forEach(r => {
        this.results.push(
          {
            x1: r.x1!,
            y1: r.y1!,
            x2: r.x2!,
            y2: r.y2!,
            features: r.features ? r.features.map(f => f) : [],
            type: FeedbackType.ERROR,
            file: this.attempt.files!.find(f => f.id === r.fileId),
            style: {},
            isEditing: true,
            tutorComment: '',
            initialTop: 0,
            initialLeft: 0
          })
      })
    }

  }

  addFeedback(type: FeedbackType) {
    this.creatingFeedbackType = type;
    this.isDrawingNow = true;
    this.tutorSelectionDiv = document.getElementById('tutorSelection')!;
  }

  setInitialOffset() {
    this.results.forEach(f => {
      f.initialTop = this.getScaledPx(f.x1);
      f.initialLeft = this.getScaledPx(f.x1);
    });
  }

  calculateStyles() {
    this.scaleCoefficient = this.imgSource.clientWidth / this.imgSource.naturalWidth;
    this.results.forEach(f => this.calculateStyle(f));
  }

  calculateStyle(feedback: any) {
    feedback.style = {
      left: this.getScaledPx(feedback.x1) + 'px',
      top: this.getScaledPx(feedback.y1) + 'px',
      width: this.getScaledPx(feedback.x2 - feedback.x1) + 'px',
      height: this.getScaledPx(feedback.y2 - feedback.y1) + 'px',
      transform: 'none'
    }
  }

  getScaledPx(origCoordinate: number): number {
    return origCoordinate * this.scaleCoefficient;
  }

  getUnscaledPx(scaledCoordinate: number): number {
    return scaledCoordinate / this.scaleCoefficient;
  }

  onDrawStart(event: MouseEvent, imgWrapper: HTMLDivElement) {
    this.stopNativeEvent(event);
    if (!this.isDrawingNow) {
      return;
    }
    this.tutorSelectionDiv.hidden = false;
    this.startCoordinates.x1 = event.clientX - imgWrapper.offsetLeft;
    this.startCoordinates.y1 = event.clientY - imgWrapper.offsetTop;
    this.reCalcTutorSelection();
  }

  onDraw(event: MouseEvent, imgWrapper: HTMLDivElement) {
    if (!this.isDrawingNow) {
      return;
    }
    this.startCoordinates.x2 = event.clientX - imgWrapper.offsetLeft;
    this.startCoordinates.y2 = event.clientY - imgWrapper.offsetTop;
    this.reCalcTutorSelection();
  }

  onDrawEnd() {
    if (!this.isDrawingNow) {
      return;
    }

    const newFeedback = {
      x1: this.getUnscaledPx(this.lastCoordinates.x1),
      y1: this.getUnscaledPx(this.lastCoordinates.y1),
      x2: this.getUnscaledPx(this.lastCoordinates.x2),
      y2: this.getUnscaledPx(this.lastCoordinates.y2),
      features: [],
      type: this.creatingFeedbackType,
      style: {},
      isEditing: true,
      comment: '',
      initialTop: 0,
      initialLeft: 0
    };
    this.calculateStyle(newFeedback);

    this.results.push(newFeedback);
    this.tutorSelectionDiv.hidden = true;
    this.isDrawingNow = false;
  }

  onDragEndPro(feedback: any, event: IPosition) {
    const width = feedback.x2 - feedback.x1;
    const height = feedback.y2 - feedback.y1;
    feedback.x1 = Math.floor(this.getUnscaledPx(event.x + feedback.initialLeft));
    feedback.y1 = Math.floor(this.getUnscaledPx(event.y + feedback.initialTop));
    feedback.x2 = Math.floor(feedback.x1 + width);
    feedback.y2 = Math.floor(feedback.y1 + height);

    if (feedback.x1 < 0) {
      feedback.x1 = 0;
    }
    if (feedback.y1 < 0) {
      feedback.y1 = 0;
    }
  }

  reCalcTutorSelection() {
    const x3 = Math.min(this.startCoordinates.x1, this.startCoordinates.x2);
    const x4 = Math.max(this.startCoordinates.x1, this.startCoordinates.x2);
    const y3 = Math.min(this.startCoordinates.y1, this.startCoordinates.y2);
    const y4 = Math.max(this.startCoordinates.y1, this.startCoordinates.y2);
    this.tutorSelectionDiv.style.left = x3 + 'px';
    this.tutorSelectionDiv.style.top = y3 + 'px';
    this.tutorSelectionDiv.style.width = x4 - x3 + 'px';
    this.tutorSelectionDiv.style.height = y4 - y3 + 'px';

    this.lastCoordinates = {x1: x3, y1: y3, x2: x4, y2: y4};
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
