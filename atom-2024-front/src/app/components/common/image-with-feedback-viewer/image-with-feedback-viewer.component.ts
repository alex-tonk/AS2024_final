import {Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TooltipModule} from 'primeng/tooltip';
import {NgForOf, NgIf, NgStyle} from '@angular/common';
import {ImageModule} from 'primeng/image';
import {Button, ButtonDirective} from 'primeng/button';
import {DropdownModule} from 'primeng/dropdown';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {InputTextModule} from 'primeng/inputtext';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {SplitterModule} from 'primeng/splitter';
import {AngularDraggableModule, IPosition} from 'angular2-draggable';
import {
  AttemptCheckResultDto,
  AttemptDto,
  AttemptFileDto,
  FeatureDto,
  LessonDto
} from '../../../gen/atom2024backend-dto';
import {MessageService} from 'primeng/api';
import {ConfigService} from '../../../services/config.service';
import {SliderModule} from 'primeng/slider';
import {MultiSelectModule} from 'primeng/multiselect';
import {TaskService} from '../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {LectureViewComponent} from '../../pages/student-cabinet/lecture-view/lecture-view.component';

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
    NgStyle,
    SliderModule,
    MultiSelectModule,
    LectureViewComponent,
    ButtonDirective
  ],
  templateUrl: './image-with-feedback-viewer.component.html',
  styleUrl: './image-with-feedback-viewer.component.css'
})
export class ImageWithFeedbackViewerComponent implements OnInit {
  private _activeIndex: number;

  @Input() get activeIndex(): number {
    return this._activeIndex;
  }

  set activeIndex(value: number) {
    this._activeIndex = value;
    if (value === this.currentIndex && this.imgSource) {
      this.onImageLoad(this.imgSource);
    }
    console.log(this.attempt);
  }

  @Input() currentIndex: number;
  @Input() resultsAIBackup: AttemptCheckResultDto[];

  @Input() isTutorMode = true;
  @Input() attempt: AttemptDto;
  @Input() file: AttemptFileDto;
  @Input() features: FeatureDto[];

  @Output() onNewErrorAdded = new EventEmitter<any>();
  @Output() onErrorDeleted = new EventEmitter<any>();

  loading = false;
  baseUrl: string;

  zoomValue = 100;
  noAutoErrors = false;

  @ViewChild('imgWrapper') imgWrapper: ElementRef;

  scaleCoefficient = 1;
  imgSource: HTMLImageElement;

  isDrawingNow = false;
  tutorSelectionDiv: HTMLElement;

  startCoordinates = {x1: 0, y1: 0, x2: 0, y2: 0};
  lastCoordinates = {x1: 0, y1: 0, x2: 0, y2: 0};
  hoveredFeedback: any;

  results: any [] = [];

  recommendations: LessonDto[];
  recommendationViewVisible = false;
  recommendationForLectureView?: LessonDto;


  // TODO Надо пофиксить ресайз, пока пофиг
  @HostListener('window:resize', ['$event'])
  onResize() {
    this.calculateStyles();
  }

  constructor(
    private messageService: MessageService,
    private configService: ConfigService,
    private taskService: TaskService
  ) {
    this.baseUrl = this.configService.baseUrl;
  }

  async ngOnInit() {
  }

  async onImageLoad(imgSource: any) {
    this.imgSource = imgSource;
    await this.init();
    this.calculateStyles();
  }

  async init() {
    try {
      if (this.isTutorMode) {
        if (!this.resultsAIBackup ||
          this.resultsAIBackup.filter(r => r.fileId === this.file.fileId).length === 0) {
          this.noAutoErrors = true;
        } else {
          this.results = this.attempt.tutorCheckResults!.filter(r => r.fileId === this.file.fileId);
        }
      } else {
        this.results = this.attempt.tutorCheckResults!.filter(r => r.fileId === this.file.fileId);
        this.recommendations = await lastValueFrom(this.taskService.getRecommendations(this.attempt.lesson?.id!));
      }
    } finally {

    }
  }

  delete(feedback: any) {
    const idx = this.results.findIndex(f => f === feedback);
    const deleted = this.results.splice(idx, 1);
    if (deleted[0] != null) {
      this.onErrorDeleted.emit(deleted[0]);
    }
  }

  restore(feedback: any) {
    Object.assign(feedback, this.resultsAIBackup.find(f => f.id === feedback.id));
    this.calculateStyles();
  }

  addFeedback() {
    this.isDrawingNow = true;
    this.tutorSelectionDiv = document.getElementById('tutorSelection' + this.currentIndex)!;
    this.messageService.add({
      severity: 'info',
      summary: 'Внимание',
      detail: 'Выделите необходимую область на картинке'
    });
  }

  calculateStyles() {
    this.scaleCoefficient = this.imgSource.clientWidth / this.imgSource.naturalWidth;
    this.results.forEach(f => this.calculateStyle(f));
  }

  calculateStyle(feedback: any) {
    feedback.style = {
      width: this.getScaledPx(feedback.x2 - feedback.x1) + 'px',
      height: this.getScaledPx(feedback.y2 - feedback.y1) + 'px',
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
      isAutomatic: false,
      fileId: this.file.fileId,
      comment: '',
      style: {}
    };
    this.calculateStyle(newFeedback);

    this.results.push(newFeedback);
    this.onNewErrorAdded.emit(newFeedback);

    this.tutorSelectionDiv.hidden = true;
    this.isDrawingNow = false;
  }

  onDragEndPro(feedback: any, event: IPosition) {
    const width = feedback.x2 - feedback.x1;
    const height = feedback.y2 - feedback.y1;
    feedback.x1 = Math.floor(this.getUnscaledPx(event.x));
    feedback.y1 = Math.floor(this.getUnscaledPx(event.y));
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

  onResizeStop(feedback: any, event: any) {
    feedback.style = event.size;
  }

  stopNativeEvent(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
  }

  onHoverFeedbackInList(feedback: any) {
    this.hoveredFeedback = feedback;
  }

  openRecommendation(rec: LessonDto) {
    this.recommendationForLectureView = rec;
    this.recommendationViewVisible = true;
  }
}
