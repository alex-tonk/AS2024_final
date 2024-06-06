import {ChangeDetectorRef, Component, EventEmitter, Input, NgZone, OnInit, Output, ViewChild} from '@angular/core';
import {Dialog, DialogModule} from "primeng/dialog";
import {NgIf} from "@angular/common";
import {SurveyAttemptAnswerDto, SurveyDto} from "../../../../gen/survey-dto";
import {ButtonModule} from "primeng/button";
import {TooltipModule} from "primeng/tooltip";
import {SurveyAttemptService, SurveyQuestionService} from "../../../../gen/survey-controllers";
import {catchError, EMPTY, lastValueFrom, Observable} from "rxjs";
import {Model, SurveyNG} from "survey-angular";
import {SurveyUtilService} from "../../../../services/survey-util.service";
import {MessageService} from "primeng/api";
import {MessageServiceKey} from "../../../../app.component";
import {Router} from "@angular/router";
import {Question} from "question";
import {HttpResponse} from "@angular/common/http";

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
export class SurveyAttemptComponent implements OnInit {

    @Input()
    set survey(value: SurveyDto) {
        this._survey = Object.assign({}, value);
        if (this.survey.lastSurveyAttempt != null) {
            this.survey.lastSurveyAttempt = Object.assign({}, this.survey.lastSurveyAttempt);
        }
    }

    get survey(): SurveyDto {
        return this._survey;
    }

    @Output()
    surveyAttemptEnd: EventEmitter<void> = new EventEmitter<void>();

    @ViewChild('dialog')
    dialog: Dialog;

    _survey: SurveyDto;
    loading = false;
    visible = true;

    constructor(private surveyAttemptService: SurveyAttemptService,
                private surveyQuestionService: SurveyQuestionService,
                private surveyUtilService: SurveyUtilService,
                private messageService: MessageService,
                private router: Router,
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
            if (this.survey.lastSurveyAttempt?.id != null) {
                this.survey.lastSurveyAttempt = await lastValueFrom(this.surveyAttemptService.getSurveyAttempt(this.survey.id, this.survey.lastSurveyAttempt.id));
            }
        } finally {
            this.loading = false;
        }
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
            this.survey.lastSurveyAttempt = await lastValueFrom(this.surveyAttemptService.beginSurveyAttempt(this.survey.id!));
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
        this.survey.lastSurveyAttempt!.answers = await lastValueFrom(this.surveyAttemptService.getAttemptAnswers(this.survey.id!, this.survey.lastSurveyAttempt!.id!));
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
                pages: this.survey.questions!.map(question => ({
                    name: 'page' + question.orderNumber,
                    elements: [
                        this.surveyUtilService.toSurveyJson(question)
                    ]
                }))
            };
            const surveyModel = new Model(surveyJson);
            surveyModel.applyTheme(this.surveyUtilService.getThemeJson());
            surveyModel.locale = 'ru';
            surveyModel.showProgressBar = 'belowHeader';
            surveyModel.progressBarType = "pages";
            surveyModel.showCompletedPage = false;
            surveyModel.progressBarShowPageNumbers = false;
            surveyModel.onProgressText.add((survey, options) => options.text = `Вопрос ${survey.currentPageNo + 1} из ${survey.pageCount}`);
            surveyModel.onUpdatePanelCssClasses.add((survey, options) => {
            });
            if (this.survey.timeLimitMinutes != null) {
                surveyModel.showTimerPanel = 'bottom';
                surveyModel.showTimerPanelMode = 'survey';
                console.log(this.survey.timeLimitMinutes * 60, (new Date().getTime() - this.survey.lastSurveyAttempt!.beginDate!.getTime()) / 1000);
                surveyModel.maxTimeToFinish = Math.max((this.survey.timeLimitMinutes * 60 - Math.round(((new Date().getTime() - this.survey.lastSurveyAttempt!.beginDate!.getTime()) / 1000))), 1);
            }
            if (this.survey.lastSurveyAttempt?.answers != null) {
                surveyModel.data = this.surveyUtilService.constructSurveyJsData(this.survey.lastSurveyAttempt.answers)
            }
            SurveyNG.render('surveyJsQuestionAttempt', {model: surveyModel});
            surveyModel.onCurrentPageChanging.add((survey, pageChange) => {
                this.ngZone.run(async () => {
                    const surveyJsQuestion: Question | undefined = (pageChange?.oldCurrentPage?.questions ?? [])[0];
                    if (surveyJsQuestion != null) {
                        const question = this.survey.questions?.find(q => q.id === Number(surveyJsQuestion.name));
                        if (question != null) {
                            const answer = this.surveyUtilService.constructSurveyAttemptAnswer(this.survey.id!, question, surveyJsQuestion.value);
                            //  TODO: don't show message on error
                            await lastValueFrom(
                                this.surveyAttemptService.saveAnswer(this.survey.id!, this.survey.lastSurveyAttempt!.id!, answer)
                            );
                        }
                    }
                });
            })
            surveyModel.onComplete.add(survey => {
                this.ngZone.run(async () => {
                    // TODO: show result on success or retry on server error
                    if (survey.data != null) {
                        this.survey.lastSurveyAttempt!.answers = Object.keys(survey.data).map(questionId => {
                            const question = this.survey.questions?.find(q => q.id === Number(questionId));
                            if (question != null) {
                                return this.surveyUtilService.constructSurveyAttemptAnswer(this.survey.id!, question, survey.data[questionId]);
                            } else {
                                return null;
                            }
                        }).filter(a => a != null) as SurveyAttemptAnswerDto[];
                    } else {
                        this.survey.lastSurveyAttempt!.answers = [];
                    }
                    this.survey.lastSurveyAttempt = await lastValueFrom(this.surveyAttemptService.finishAttempt(this.survey.id!, this.survey.lastSurveyAttempt!.id!, this.survey.lastSurveyAttempt!));
                    this.surveyAttemptEnd.emit();
                });
            });
        });
    }
}
