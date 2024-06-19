import {Component, Input, OnInit} from '@angular/core';
import {getField} from '../../services/field-accessor';
import {Button} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {ColumnFilterWrapperComponent} from '../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {InputTextModule} from 'primeng/inputtext';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {MessageService, PrimeTemplate} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {
  AttemptCheckResultDto,
  AttemptDto,
  AttemptFileDto,
  FeatureDto,
  LessonDto,
  TaskDto,
  TopicDto
} from '../../gen/atom2024backend-dto';
import {Column} from '../common/table/Column';
import {FormsModule} from '@angular/forms';
import {ExportTable} from '../common/table/ExportTable';
import {MenuModule} from 'primeng/menu';
import {AttemptService, FeatureService} from '../../gen/atom2024backend-controllers';
import {firstValueFrom, lastValueFrom} from 'rxjs';
import {TagModule} from 'primeng/tag';
import {TagStyleService} from '../../services/tag-style-service';
import {DialogModule} from 'primeng/dialog';
import {
  ImageWithFeedbackViewerComponent
} from '../common/image-with-feedback-viewer/image-with-feedback-viewer.component';
import {AttemptStatus, Mark} from '../../gen/atom2024backend-enums';
import {TaskAttemptComponent} from '../pages/student-cabinet/task-attempt/task-attempt.component';
import {TabViewModule} from 'primeng/tabview';
import {UserListComponent} from '../pages/admin-panel/user-list/user-list.component';
import {DropdownModule} from 'primeng/dropdown';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {MarkLocale} from '../../models/enum/locale/MarkLocale';

@Component({
  selector: 'app-attempt',
  standalone: true,
  imports: [
    Button,
    CheckboxModule,
    ColumnFilterWrapperComponent,
    InputTextModule,
    NgForOf,
    NgIf,
    PrimeTemplate,
    TableModule,
    TooltipModule,
    FormsModule,
    AsyncPipe,
    MenuModule,
    DialogModule,
    ImageWithFeedbackViewerComponent,
    MenuModule,
    TagModule,
    TaskAttemptComponent,
    TabViewModule,
    UserListComponent,
    DropdownModule,
    InputTextareaModule
  ],
  templateUrl: './attempt.component.html',
  styleUrl: './attempt.component.css'
})
export class AttemptComponent implements OnInit {
  activeIndex = 0;
  resultsAIBackup: AttemptCheckResultDto[];

  @Input()
  mode: AttemptListMode = AttemptListMode.TUTOR;

  @Input()
  userId: number | null;

  @Input()
  status: AttemptStatus | null;

  attempts: AttemptDto[] = [];
  features: FeatureDto[] = [];
  loading = false;
  selectedAttempt?: AttemptDto;
  filter = false;

  readOnlyTaskMode = true;

  checkingDialogVisible = false;
  finalCheckingDialogVisible = false;
  checkingAttempt: AttemptDto;
  checkingAttemptFiles: AttemptFileDto[];
  tutorMarkOptions = Object.values(Mark)
    .map(value => ({value: value, label: MarkLocale[value]}));

  allColumns: Column[] = [
    {
      header: 'ID',
      field: 'id',
      type: 'numeric',
      width: 10
    },
    {
      header: 'Обучающийся',
      field: 'user.fullName',
      width: 25
    },
    {
      header: 'Статус',
      field: 'statusLocale',
      type: 'status'
    },
    {
      header: 'Оценка наставника',
      field: 'tutorMarkLocale',
      type: 'mark'
    },
    {
      header: 'Тема',
      field: 'topic.title',
      width: 10
    },
    {
      header: 'Учебный материал',
      field: 'lesson.title',
      width: 10
    },
    {
      header: 'Задание',
      field: 'task.title',
      width: 10
    },
    {
      header: 'Сложность задания',
      field: 'task.difficultyLocale',
      type: 'difficulty'
    },
    {
      header: 'Статус обработки ИИ',
      field: 'autoStatus',
      type: 'autoStatus'
    },
    {
      header: 'Дата начала выполнения',
      field: 'formattedStartDate',
      type: 'date'
    },
    {
      header: 'Дата завершения выполнения',
      field: 'formattedEndDate',
      type: 'date'
    },
    {
      header: 'Оценка системы ИИ',
      field: 'autoMarkLocale',
      type: 'mark'
    },
    {
      header: 'Архив',
      field: 'archived',
      type: 'boolean'
    }
  ];

  columns: Column[] = [];

  taskAttemptVisible = false;
  taskAttemptFormData?: { topic: TopicDto; lesson: LessonDto; task: TaskDto };
  taskAttemptId?: number;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  protected readonly getField = getField;
  protected readonly ExportTable = ExportTable;

  constructor(private attemptService: AttemptService,
              private tagStyleService: TagStyleService,
              private messageService: MessageService,
              private featureService: FeatureService) {
  }

  async getAttemptsFromApi() {
    this.loading = true;
    this.selectedAttempt = undefined;
    try {
      this.attempts = await lastValueFrom(this.attemptService.getAttempts(this.userId, this.status));
    } finally {
      this.loading = false;
    }
  }

  getStatusStyle(statusLocale: string) {
    return {...this.tagStyleService.getStatusStyle(statusLocale), width: '100%'};
  }

  getAutoStatusStyle(autoStatus: string) {
    return {...this.tagStyleService.getAutoStatusStyle(autoStatus), width: '100%'};
  }

  getMarkStyle(markLocale: string) {
    return {...this.tagStyleService.getMarkStyle(markLocale), width: '100%'};
  }

  getDifficultyStyle(difficultyLocale: string) {
    return {...this.tagStyleService.getDifficultyStyle(difficultyLocale), width: '100%'};
  }

  async ngOnInit() {
    try {
      this.loading = true;
      this.columns = this.allColumns;
      if (this.mode === AttemptListMode.STUDENT) {
        this.columns = this.columns
          .filter(c => [
            'statusLocale',
            'topic.title',
            'lesson.title',
            'task.title',
            'tutorMarkLocale',
            'task.difficultyLocale',
            'formattedStartDate',
            'formattedEndDate']
            .includes(c.field));
      }
      await this.getAttemptsFromApi();
      this.features = await lastValueFrom(this.featureService.getFeatures());
    } finally {
      this.loading = false;
    }
  }

  async checkAttempt() {
    this.readOnlyTaskMode = false;
    if (!this.selectedAttempt) {
      return;
    }
    if (this.selectedAttempt.autoStatus === 'В обработке') {
      this.messageService.add({
        severity: 'warn',
        sticky: true,
        summary: 'Внимание',
        detail: 'Система ИИ все еще проверяет работу'
      });
    }
    this.loading = true;
    try {
      this.checkingAttempt = await lastValueFrom(this.attemptService.getAttempt(this.selectedAttempt.id!));
      if (!this.checkingAttempt) {
        this.messageService.add({severity: 'error', summary: 'Внимание', detail: 'Работа не найдена в БД'});
        return;
      }
      if (this.checkingAttempt.autoCheckResults && this.checkingAttempt.autoCheckResults.length > 0) {
        this.resultsAIBackup = this.checkingAttempt.autoCheckResults.map(r => r)
        this.resultsAIBackup.forEach(r => r.comment =  r.isAutomatic ? 'Дефект найденный ИИ' : r.comment);
      }

      this.checkingAttempt.tutorCheckResults = this.checkingAttempt.autoCheckResults ?? [];

      this.checkingAttemptFiles = this.checkingAttempt.files!;
      this.checkingDialogVisible = true;
    } finally {
      this.loading = false;
    }
  }

  async completeAttemptByTutor() {
    this.checkingAttempt.tutorMark = this.checkingAttempt.autoMark;
    this.finalCheckingDialogVisible = true;
  }


  async finalCompleteAttemptByTutor() {
    this.loading = true;
    try {
      if (!this.checkingAttempt) {
        return;
      }
      const result = await firstValueFrom(this.attemptService.setTutorMark(this.checkingAttempt.id!, this.checkingAttempt));
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Задание проверено'});
      this.checkingDialogVisible = false;
      this.finalCheckingDialogVisible = false;
      await this.getAttemptsFromApi();
    } finally {
      this.loading = false;
    }
  }

  onTutorErrorAdded(newError: any) {
    console.log('onAdded');
    this.checkingAttempt.tutorCheckResults!.push(newError);
  }

  protected readonly AttemptListMode = AttemptListMode;
  protected readonly AttemptStatus = AttemptStatus;

  continueAttempt() {
    this.taskAttemptFormData = {
      topic: this.selectedAttempt!.topic!,
      lesson: this.selectedAttempt!.lesson!,
      task: this.selectedAttempt!.task!
    };
    this.taskAttemptId = this.selectedAttempt!.id!;
    this.taskAttemptVisible = true;
  }

  onTaskAttemptClose() {
    this.taskAttemptVisible = false;
    this.taskAttemptFormData = undefined;
    this.taskAttemptId = undefined;
  }

  async showResults() {
    if (!this.selectedAttempt) {
      return;
    }
    if (!this.selectedAttempt.tutorMark) {
      this.messageService.add({severity: 'warn', summary: 'Внимание', detail: 'Задание еще проверяется'});
      return;
    }
    try {
      this.loading = true;
      this.readOnlyTaskMode = true;
      this.checkingAttempt = await lastValueFrom(this.attemptService.getAttempt(this.selectedAttempt.id!));
      if (!this.checkingAttempt) {
        this.messageService.add({severity: 'error', summary: 'Внимание', detail: 'Работа не найдена в БД'});
        return;
      }
      if (this.checkingAttempt.status !== AttemptStatus.DONE) {
        this.messageService.add({severity: 'warn', summary: 'Внимание', detail: 'Работа не проверялась'});
        return;
      }

      this.checkingAttemptFiles = this.checkingAttempt.files!;
      this.checkingDialogVisible = true;
    } finally {
      this.loading = false;
    }
  }
}

export enum AttemptListMode {
  STUDENT, TUTOR
}
