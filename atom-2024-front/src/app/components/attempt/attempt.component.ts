import {Component, OnInit} from '@angular/core';
import {getField} from '../../services/field-accessor';
import {Button} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {ColumnFilterWrapperComponent} from '../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {InputTextModule} from 'primeng/inputtext';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {PrimeTemplate} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {AttemptDto} from '../../gen/atom2024backend-dto';
import {Column} from '../common/table/Column';
import {FormsModule} from '@angular/forms';
import {ExportTable} from '../common/table/ExportTable';
import {MenuModule} from 'primeng/menu';
import {AttemptService} from '../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {DialogModule} from 'primeng/dialog';
import {
  ImageWithFeedbackViewerComponent
} from '../common/image-with-feedback-viewer/image-with-feedback-viewer.component';

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
    ImageWithFeedbackViewerComponent
  ],
  templateUrl: './attempt.component.html',
  styleUrl: './attempt.component.css'
})
export class AttemptComponent implements OnInit {

  attempts: AttemptDto[] = [];
  loading = false;
  selectedAttempt?: AttemptDto;
  filter = false;

  checkingDialogVisible = false;
  checkingAttemptId: number;
  checkingAttempt: AttemptDto;

  columns: Column[] = [
    {
      header: 'ID',
      field: 'id',
      type: 'numeric',
      width: 10
    }, {
      header: 'Обучающийся',
      field: 'user.fullName',
      width: 25
    }, {
      header: 'Тема',
      field: 'topic.title'
    }, {
      header: 'Учебный материал',
      field: 'lesson.title'
    }, {
      header: 'Задание',
      field: 'task.title'
    }, {
      header: 'Статус',
      field: 'statusLocale',
    },
    {
      header: 'Статус обработки ИИ',
      field: 'autoStatus',
    },
    {
      header: 'Дата начала выполнения',
      field: 'formattedStartDate',
      type: 'date'
    }, {
      header: 'Дата завершения выполнения',
      field: 'formattedEndDate',
      type: 'date'
    }, {
      header: 'Оценка системы ИИ',
      field: 'autoMarkLocale',
    }, {
      header: 'Оценка наставника',
      field: 'tutorMarkLocale',
    }, {
      header: 'Архив',
      field: 'archived',
      type: 'boolean'
    }
  ];

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  protected readonly getField = getField;
  protected readonly ExportTable = ExportTable;

  constructor(private attemptService: AttemptService) {
  }

  async getAttemptsFromApi() {
      this.loading = true;
      this.selectedAttempt = undefined;
      try {
        this.attempts = await lastValueFrom(this.attemptService.getAttempts());
      } finally {
        this.loading = false;
      }
  }

  async ngOnInit() {
    try {
      this.loading = true;
      await this.getAttemptsFromApi();
    } finally {
      this.loading = false;
    }
  }

  checkAttempt() {
    if (!this.selectedAttempt) {
      return;
    }
    this.checkingAttemptId = this.selectedAttempt.id!
    this.checkingAttempt = Object.assign({}, this.selectedAttempt);
    this.checkingDialogVisible = true;
  }
}
