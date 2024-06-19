import {Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {DialogModule} from 'primeng/dialog';
import {InputSwitchModule} from 'primeng/inputswitch';
import {MarkdownComponent, provideMarkdown} from 'ngx-markdown';
import {AsyncPipe, NgForOf, NgIf, NgTemplateOutlet} from '@angular/common';
import {MessageService, PrimeTemplate} from 'primeng/api';
import {StepperModule} from 'primeng/stepper';
import {AttemptDto, LessonDto, SupplementDto, TaskDto, TopicDto} from '../../../../gen/atom2024backend-dto';
import {ConfigService} from '../../../../services/config.service';
import {FileService} from '../../../../services/file.service';
import {debounceTime, distinctUntilChanged, fromEvent, lastValueFrom, Observable, of} from 'rxjs';
import FileSaver from 'file-saver';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {AttemptService} from '../../../../gen/atom2024backend-controllers';
import {AttemptStatus} from '../../../../gen/atom2024backend-enums';
import {PanelModule} from 'primeng/panel';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {FileUpload, FileUploadHandlerEvent, FileUploadModule} from 'primeng/fileupload';
import {TabViewModule} from 'primeng/tabview';
import {UserListComponent} from '../../admin-panel/user-list/user-list.component';
import {TooltipModule} from 'primeng/tooltip';
import {RouterLink} from '@angular/router';
import {setInterval} from 'core-js';
import {NgxMarkjsModule} from 'ngx-markjs';
import {map} from 'rxjs/operators';
import {MessageServiceKey} from '../../../../app.component';
import {TagModule} from 'primeng/tag';
import {getField} from '../../../../services/field-accessor';
import {TagStyleService} from '../../../../services/tag-style-service';

@Component({
  selector: 'app-task-attempt',
  standalone: true,
  imports: [
    DialogModule,
    InputSwitchModule,
    MarkdownComponent,
    NgForOf,
    NgIf,
    PrimeTemplate,
    StepperModule,
    FormsModule,
    Button,
    PanelModule,
    InputTextareaModule,
    FileUploadModule,
    TabViewModule,
    UserListComponent,
    TooltipModule,
    RouterLink,
    AsyncPipe,
    NgxMarkjsModule,
    NgTemplateOutlet,
    TagModule
  ],
  templateUrl: './task-attempt.component.html',
  styleUrl: './task-attempt.component.css',
  providers: [provideMarkdown()]
})
export class TaskAttemptComponent implements OnInit, OnDestroy {
  @Input()
  formData: { topic: TopicDto, lesson: LessonDto, task: TaskDto };

  @Input()
  attemptId?: number;

  @Input()
  taskAttempt: AttemptDto;

  @Output()
  taskAttemptClose: EventEmitter<boolean> = new EventEmitter<boolean>();

  get task() {
    return this.formData.task;
  }

  get lesson() {
    return this.formData.lesson;
  }

  get topic() {
    return this.formData.topic;
  }

  visible = true;
  loading = false;
  original = false;
  content: string;
  beautifiedContent: string;
  lessonOriginal = false;
  lessonBeautifiedContent: string;
  stepperIndex = 0;
  stepsCount = 3;

  remainingTimeInterval: number;
  remainingTime: string;

  @ViewChild('search', {static: false}) searchElemRef: ElementRef | undefined;
  searchText$: Observable<string | null> = of(null);
  searchConfig = {separateWordSearch: false};

  searchResults: HTMLCollectionOf<HTMLElement>;
  currentResult = 0;

  constructor(private configService: ConfigService,
              private fileService: FileService,
              private messageService: MessageService,
              private attemptService: AttemptService,
              protected tagStyleService: TagStyleService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      this.content = (this.task?.content ?? '').replaceAll('<baseUrl>', this.configService.baseUrl);
      this.beautifiedContent = this.content
        .replaceAll(/!\[/g, '<br>![')
        .replaceAll('<br><br>', '<br>')
        .replaceAll('<br>', '\n');
      this.lessonBeautifiedContent = this.lesson.content
        ?.replaceAll(/!\[/g, '<br>![')
        ?.replaceAll('<br><br>', '<br>')
        ?.replaceAll('<br>', '\n')!;
      let lastAttempt = await lastValueFrom(this.attemptService.getLastAttempt(this.topic.id!, this.lesson.id!, this.task.id!));
      if (this.attemptId != null) {
        const attempt = await lastValueFrom(this.attemptService.getAttempt(this.attemptId));
        if (attempt == null || attempt.status !== AttemptStatus.IN_PROGRESS) {
          this.messageService.add({
            key: MessageServiceKey.OK,
            sticky: true,
            severity: 'error',
            summary: 'Ошибка',
            detail: 'Попытка уже завершена'
          });
          this.taskAttemptClose.emit();
          return;
        }
        lastAttempt = attempt;
      }
      // TODO: viewAttempt after check
      if (lastAttempt != null && lastAttempt.status === AttemptStatus.IN_PROGRESS) {
        this.taskAttempt = lastAttempt;
        this.stepperIndex = 2;
      }
      this.remainingTimeInterval = setInterval(() => {
        this.remainingTime = this.taskAttempt?.status === AttemptStatus.IN_PROGRESS ? this.calcRemainingTime(this.taskAttempt) : `${this.task.time} мин.`;
      }, 1);
      this.subscribeToSearchBarEvents();
    } finally {
      this.loading = false;
    }
  }

  subscribeToSearchBarEvents() {
    setTimeout(() => {
      if (!this.searchElemRef?.nativeElement) return;

      this.searchText$ = fromEvent<Event>(this.searchElemRef?.nativeElement, 'keyup').pipe(
        map((e: Event) => {
          setTimeout(() => {
            this.searchResults = document.getElementsByTagName('mark');
            this.currentResult = -1;
            this.scrollToPrevResult();
          }, 350);
          return (e.target as HTMLInputElement).value;
        }),
        debounceTime(300),
        distinctUntilChanged()
      );
    });
  }

  ngOnDestroy() {
    clearInterval(this.remainingTimeInterval);
  }

  async downloadTaskFile(s: SupplementDto) {
    if (s?.fileId == null) {
      return;
    }

    this.loading = true;
    try {
      let blob = await lastValueFrom(this.fileService.getLessonFile(s.fileId));
      FileSaver.saveAs(blob, s.title || s.fileName);
    } finally {
      this.loading = false;
    }
  }

  getTimeLimitString() {
    if (this.task.time == null) {
      return '';
    }
    if (this.task.time >= 60) {
      const timeLimitHours = Math.round(this.task.time / 60);
      let hoursLabel = 'час';
      if ([11, 12, 13, 14].includes(timeLimitHours % 100)) {
        hoursLabel = 'часов';
      } else {
        switch (timeLimitHours % 10) {
          case 1:
            hoursLabel = 'час';
            break;
          case 2:
          case 3:
          case 4:
            hoursLabel = 'часа';
            break;
          default:
            hoursLabel = 'часов';
        }
        hoursLabel = 'час';
      }
      if ((this.task.time % 60) == 0) {
        return `${timeLimitHours} ${hoursLabel}`;
      }
      return `${timeLimitHours} ${hoursLabel} ${this.task.time % 60} мин.`;
    } else {
      return `${this.task.time} мин.`
    }
  }

  async startTaskAttempt() {
    this.loading = true;
    try {
      this.taskAttempt = await lastValueFrom(this.attemptService.startNewAttempt(this.topic.id!, this.lesson.id!, this.task.id!));
      this.messageService.add({severity: 'info', summary: 'Внимание', detail: 'Вы начали выполнение задания'});
    } finally {
      setTimeout(() => {
        this.loading = false
      }, 500);
    }
  }

  nextStep() {
    this.stepperIndex += 1;
  }

  previousStep() {
    this.stepperIndex -= 1;
  }

  async sendToCheck() {
    this.loading = false;
    try {
      await lastValueFrom(this.attemptService.finishAttempt(this.taskAttempt.id!, this.taskAttempt));
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Вы отправили задание на проверку'});
      this.taskAttemptClose.emit(true);
    } finally {
      this.loading = false;
    }
  }

  async addAttemptFile(event: FileUploadHandlerEvent, fileUpload: FileUpload) {
    this.loading = true;
    try {
      const fileName = event.files[0].name;
      const fileId = await lastValueFrom(this.fileService.uploadAttemptFile(event.files[0]));
      this.taskAttempt.files = [...(this.taskAttempt.files ?? []), {fileId: fileId, fileName: fileName}];
    } finally {
      setTimeout(() => fileUpload.clear());
      this.loading = false;
    }
  }

  onFileRemove(index: number) {
    if (this.taskAttempt.files) {
      this.taskAttempt.files.splice(index, 1);
    }
  }

  private calcRemainingTime(attempt: AttemptDto) {
    let diffSecs = attempt?.task?.time! * 60 - (new Date().getTime() - attempt?.startDate?.getTime()!) / 1000;
    if (diffSecs < 0) diffSecs = 0;
    const minutes = Math.floor(diffSecs / 60);
    const seconds = diffSecs - minutes * 60;
    return `${minutes.toString()} мин. ${seconds.toFixed(0)} сек.`;
  }

  scrollToPrevResult() {
    this.searchResults.item(this.currentResult)?.classList.remove('current-mark');
    this.currentResult = (this.currentResult + 1) % this.searchResults.length;
    this.searchResults.item(this.currentResult)?.classList.add('current-mark');
    this.searchResults.item(this.currentResult)?.scrollIntoView({behavior: 'smooth'});
  }

  scrollToNextResult() {
    this.searchResults.item(this.currentResult)?.classList.remove('current-mark');
    this.currentResult = this.currentResult - 1;
    if (this.currentResult < 0) this.currentResult += this.searchResults.length;
    this.searchResults.item(this.currentResult)?.classList.add('current-mark');
    this.searchResults.item(this.currentResult)?.scrollIntoView({behavior: 'smooth'});
  }

  protected readonly getField = getField;
}
