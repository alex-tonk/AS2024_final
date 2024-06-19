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
import {lastValueFrom} from 'rxjs';
import {ConfirmationService, MessageService} from 'primeng/api';
import {UserService} from '../../../services/user.service';
import {SurveyAttemptDto, SurveyDto} from '../../../models/survey-dto';
import {MessageServiceKey} from '../../../app.component';

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
              private confirmationService: ConfirmationService,
              private messageService: MessageService) {
  }

  async ngOnInit(): Promise<void> {
  }

  createSurvey() {
    this.surveyRegistrationId = undefined;
    this.surveyRegistrationVisible = true;
  }

  async onSurveyRegistrationResult(survey: SurveyDto | null) {
    if (survey != null) {
      this.messageService.add({
        key: MessageServiceKey.OK,
        sticky: true,
        severity: 'warn',
        summary: 'Демо',
        detail: 'Функционал создания тестирований доступен в PRO-версии'
      });
    }
    this.surveyRegistrationVisible = false;
    this.surveyRegistrationId = undefined;
  }

}
