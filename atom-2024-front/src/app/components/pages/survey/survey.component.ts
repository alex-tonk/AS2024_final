import {Component, OnInit} from '@angular/core';
import {DataViewModule} from 'primeng/dataview';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {DividerModule} from 'primeng/divider';
import {CardModule} from 'primeng/card';
import {ProgressBarModule} from 'primeng/progressbar';
import {ButtonModule} from 'primeng/button';
import {TooltipModule} from 'primeng/tooltip';
import {ToolbarModule} from 'primeng/toolbar';
import {Router} from '@angular/router';
import {SurveyRegistrationComponent} from './survey-registration/survey-registration.component';
import {SurveyAttemptDto, SurveyDto} from '../../../gen/survey-dto';
import {SurveyService} from '../../../gen/survey-controllers';
import {lastValueFrom} from 'rxjs';
import {ConfirmationService, MessageService} from 'primeng/api';
import {SurveyAttemptComponent} from './survey-attempt/survey-attempt.component';
import {SurveyReviewComponent} from './survey-review/survey-review.component';
import {UserService} from '../../../services/user.service';

@Component({
  selector: 'app-survey',
  standalone: true,
  imports: [
    DataViewModule,
    NgClass,
    NgForOf,
    DividerModule,
    NgIf,
    CardModule,
    ProgressBarModule,
    ButtonModule,
    TooltipModule,
    ToolbarModule,
    SurveyRegistrationComponent,
    SurveyAttemptComponent,
    SurveyReviewComponent
  ],
  templateUrl: './survey.component.html',
  styleUrl: './survey.component.css'
})
export class SurveyComponent implements OnInit {
  surveys: SurveyDto[] = [];
  loading = false;

  surveyRegistrationVisible = false;
  surveyRegistrationId?: number;

  surveyAttemptVisible = false;
  surveyForAttempt?: { survey: SurveyDto, surveyAttempt?: SurveyAttemptDto };

  surveyReviewVisible = false;
  surveyAttemptForReview?: SurveyAttemptDto;

  constructor(protected userService: UserService,
              private surveyService: SurveyService,
              private confirmationService: ConfirmationService,
              private messageService: MessageService) {
  }

  async ngOnInit(): Promise<void> {
    await this.reload();
  }

  async reload() {
    this.loading = true;
    try {
      this.surveys = await lastValueFrom(this.surveyService.getSurveys());
    } finally {
      this.loading = false;
    }
  }

  createSurvey() {
    this.surveyRegistrationId = undefined;
    this.surveyRegistrationVisible = true;
  }

  editSurvey(survey: SurveyDto) {
    this.surveyRegistrationId = survey.id;
    this.surveyRegistrationVisible = true;
  }

  async onSurveyRegistrationResult(survey: SurveyDto | null) {
    if (survey != null) {
      this.loading = true;
      try {
        if (survey.id != null) {
          await lastValueFrom(this.surveyService.updateSurvey(survey.id, survey));
          this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Изменения сохранены'});
        } else {
          await lastValueFrom(this.surveyService.createSurvey(survey));
          this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Тестирование создано'});
        }
        await this.reload();
      } finally {
        this.loading = false;
      }
    }
    this.surveyRegistrationVisible = false;
    this.surveyRegistrationId = undefined;
  }

  confirmDeleteSurvey(event: Event, survey: SurveyDto) {
    this.confirmationService.confirm({
      key: 'popup',
      target: event.target as EventTarget,
      icon: 'pi pi-info-circle',
      message: 'Вы действительно хотите удалить выбранное тестирование?',
      accept: () => this.deleteSurvey(survey)
    });
  }

  async deleteSurvey(survey: SurveyDto) {
    this.loading = true;
    try {
      await lastValueFrom(this.surveyService.deleteSurvey(survey.id!));
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Тестирование удалено'});
      await this.reload();
    } finally {
      this.loading = false;
    }
  }

  beginSurvey(survey: SurveyDto) {
    this.surveyForAttempt = {survey: survey, surveyAttempt: survey.lastSurveyAttempt};
    this.surveyAttemptVisible = true;
  }

  async onSurveyAttemptEnd() {
    this.surveyAttemptVisible = false;
    this.surveyForAttempt = undefined;
    this.loading = true;
    try {
      await this.reload();
    } finally {
      this.loading = false;
    }
  }

  reviewSurveyAttempt(survey: SurveyDto) {
    if (survey?.lastSurveyAttempt?.finishDate == null) {
      return;
    }

    this.surveyAttemptForReview = survey.lastSurveyAttempt;
    this.surveyReviewVisible = true;
  }

  onSurveyReviewEnd() {
    this.surveyReviewVisible = false;
    this.surveyAttemptForReview = undefined;
  }

  getCorrectPercentage(survey: SurveyDto) {
    return (survey.lastSurveyAttempt?.correctAnswerCount != null && survey?.questionCount != null)
      ? Math.round(100 * survey.lastSurveyAttempt.correctAnswerCount / survey.questionCount)
      : 0;
  }

  getCorrectPercentageColor(survey: SurveyDto) {
    const percentage = this.getCorrectPercentage(survey);
    if (percentage < 60) {
      return 'var(--red-color)';
    } else if (percentage >= 60 && percentage < 80) {
      return 'var(--yellow-color)';
    } else {
      return 'var(--green-color)';
    }
  }
}
