import {Component, EventEmitter, Input, NgZone, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Dialog, DialogModule} from 'primeng/dialog';
import {NgIf} from '@angular/common';
import {SurveyAttemptAnswerDto, SurveyAttemptDto, SurveyDto} from '../../../../gen/survey-dto';
import {ButtonModule} from 'primeng/button';
import {TooltipModule} from 'primeng/tooltip';
import {SurveyAttemptService, SurveyQuestionService} from '../../../../gen/survey-controllers';
import {lastValueFrom} from 'rxjs';
import {Model, SurveyNG} from 'survey-angular';
import {SurveyUtilService} from '../../../../services/survey-util.service';
import {MessageService} from 'primeng/api';
import {MessageServiceKey} from '../../../../app.component';
import {Question} from 'question';

@Component({
  selector: 'app-survey-attempt',
  standalone: true,
  imports: [
    DialogModule,
    NgIf,
    ButtonModule,
    TooltipModule
  ],
  templateUrl: './survey-attempt.component.html',
  styleUrl: './survey-attempt.component.css'
})
export class SurveyAttemptComponent implements OnInit, OnDestroy {

  @Input()
  set surveyForAttempt(value: { survey: SurveyDto, surveyAttempt?: SurveyAttemptDto }) {
    this.survey = Object.assign({}, value.survey);
    if (value.surveyAttempt != null) {
      this.surveyAttempt = Object.assign({}, value.surveyAttempt);
    }
  }

  @Output()
  surveyAttemptEnd: EventEmitter<void> = new EventEmitter<void>();

  @ViewChild('dialog')
  dialog: Dialog;

  survey: SurveyDto;
  surveyAttempt?: SurveyAttemptDto;
  loading = false;
  visible = true;
  surveyModel?: Model;

  constructor(private surveyAttemptService: SurveyAttemptService,
              private surveyQuestionService: SurveyQuestionService,
              private surveyUtilService: SurveyUtilService,
              private messageService: MessageService,
              private ngZone: NgZone) {
  }


  async ngOnInit() {
    this.loading = true;
    try {
      if (this.survey?.id == null) {
        this.messageService.add(
          {
            severity: 'error',
            summary: 'Ошибка',
            detail: 'Тестирование не найдено',
            key: MessageServiceKey.OK,
            sticky: true
          }
        );
        this.surveyAttemptEnd.emit();
        return;
      }
      if (this.surveyAttempt?.id != null) {
        this.surveyAttempt = await lastValueFrom(this.surveyAttemptService.getSurveyAttempt(this.survey.id, this.surveyAttempt.id));
      }
    } finally {
      this.loading = false;
    }
  }

  ngOnDestroy() {
    this.surveyModel?.onComplete.clear();
    this.surveyModel?.onCurrentPageChanging.clear();
  }

  getTimeLimitString() {
    if (this.survey.timeLimitMinutes == null) {
      return '';
    }
    if (this.survey.timeLimitMinutes >= 60) {
      const timeLimitHours = Math.round(this.survey.timeLimitMinutes / 60);
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
      if ((this.survey.timeLimitMinutes % 60) == 0) {
        return `${timeLimitHours} ${hoursLabel}`;
      }
      return `${timeLimitHours} ${hoursLabel} ${this.survey.timeLimitMinutes % 60} мин.`;
    } else {
      return `${this.survey.timeLimitMinutes} мин.`
    }
  }

  async beginSurveyAttempt() {
    this.loading = true;
    try {
      this.surveyAttempt = await lastValueFrom(this.surveyAttemptService.beginSurveyAttempt(this.survey.id!));
      await this.goToSurvey();
    } finally {
      this.loading = false;
    }
  }

  async continueAttempt() {
    this.loading = true;
    try {
      await this.goToSurvey();
    } finally {
      this.loading = false;
    }
  }

  private async goToSurvey() {
    this.survey.questions = await lastValueFrom(this.surveyQuestionService.getSurveyQuestions(this.survey.id!));
    this.surveyAttempt!.answers = await lastValueFrom(this.surveyAttemptService.getAttemptAnswers(this.survey.id!, this.surveyAttempt!.id!));
    if ((this.survey.questions ?? []).length == 0) {
      this.messageService.add(
        {
          severity: 'error',
          summary: 'Ошибка',
          detail: 'Не найдены вопросы тестирования',
          key: MessageServiceKey.OK,
          sticky: true
        }
      );
    }
    setTimeout(() => {
      const surveyJson = {
        width: '95%',
        pages: this.survey.questions!.map(question => ({
          name: 'page' + question.orderNumber,
          elements: [
            this.surveyUtilService.toSurveyJson(question, {shuffleOptions: true})
          ]
        }))
      };
      this.surveyModel = new Model(surveyJson);
      this.surveyModel.applyTheme(this.surveyUtilService.getThemeJson());
      this.surveyModel.locale = 'ru';
      this.surveyModel.progressBarInheritWidthFrom = 'survey';
      if (this.survey.questions!.length > 1) {
        this.surveyModel.showProgressBar = 'belowHeader';
        this.surveyModel.progressBarType = 'pages';
        this.surveyModel.progressBarShowPageNumbers = false;
      }
      this.surveyModel.showCompletedPage = false;
      this.surveyModel.onProgressText.add((survey, options) => options.text = `Вопрос ${survey.currentPageNo + 1} из ${survey.pageCount}`);
      this.surveyModel.onUpdatePanelCssClasses.add((survey, options) => {
      });
      if (this.survey.timeLimitMinutes != null) {
        this.surveyModel.showTimerPanel = 'bottom';
        this.surveyModel.showTimerPanelMode = 'survey';
        this.surveyModel.maxTimeToFinish = Math.max((this.survey.timeLimitMinutes * 60 - Math.round(((new Date().getTime() - this.surveyAttempt!.beginDate!.getTime()) / 1000))), 1);
      }
      if (this.surveyAttempt?.answers != null) {
        this.surveyModel.data = this.surveyUtilService.constructSurveyJsData(this.surveyAttempt.answers)
      }
      SurveyNG.render('surveyJsQuestionAttempt', {model: this.surveyModel});
      this.surveyModel.onCurrentPageChanging.add((survey, pageChange) => {
        this.ngZone.run(async () => {
          const surveyJsQuestion: Question | undefined = (pageChange?.oldCurrentPage?.questions ?? [])[0];
          if (surveyJsQuestion != null) {
            const question = this.survey.questions?.find(q => q.id === Number(surveyJsQuestion.name));
            if (question != null) {
              const answer = this.surveyUtilService.constructSurveyAttemptAnswer(this.survey.id!, question, surveyJsQuestion.value);
              //  TODO: don't show message on error
              await lastValueFrom(
                this.surveyAttemptService.saveAnswer(this.survey.id!, this.surveyAttempt!.id!, answer)
              );
            }
          }
        });
      })
      this.surveyModel.onComplete.add(survey => {
        this.ngZone.run(async () => {
          // TODO: show result on success or retry on server error
          if (survey.data != null) {
            this.surveyAttempt!.answers = Object.keys(survey.data).map(questionId => {
              const question = this.survey.questions?.find(q => q.id === Number(questionId));
              if (question != null) {
                return this.surveyUtilService.constructSurveyAttemptAnswer(this.survey.id!, question, survey.data[questionId]);
              } else {
                return null;
              }
            }).filter(a => a != null) as SurveyAttemptAnswerDto[];
          } else {
            this.surveyAttempt!.answers = [];
          }
          this.surveyAttempt = await lastValueFrom(this.surveyAttemptService.finishAttempt(this.survey.id!, this.surveyAttempt!.id!, this.surveyAttempt!));
          this.surveyAttemptEnd.emit();
        });
      });
    });
  }
}
