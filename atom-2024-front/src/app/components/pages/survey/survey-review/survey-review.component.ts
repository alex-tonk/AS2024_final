import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Button} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {NgIf} from '@angular/common';
import {SurveyAttemptDto, SurveyQuestionDto} from '../../../../gen/survey-dto';
import {MessageServiceKey} from '../../../../app.component';
import {lastValueFrom} from 'rxjs';
import {MessageService} from 'primeng/api';
import {Model, SurveyNG} from 'survey-angular';
import {SurveyUtilService} from '../../../../services/survey-util.service';
import {SurveyAttemptService, SurveyQuestionService} from '../../../../gen/survey-controllers';
import {Question} from 'question';
import {SurveyQuestionType} from '../../../../gen/survey-enums';

@Component({
  selector: 'app-survey-review',
  standalone: true,
  imports: [
    Button,
    DialogModule,
    NgIf
  ],
  templateUrl: './survey-review.component.html',
  styleUrl: './survey-review.component.css'
})
export class SurveyReviewComponent implements OnInit {
  @Input()
  set surveyAttempt(value: SurveyAttemptDto | undefined) {
    if (value != null) {
      this._surveyAttempt = Object.assign({}, value);
    } else {
      this._surveyAttempt = undefined;
    }
  }

  get surveyAttempt(): SurveyAttemptDto | undefined {
    return this._surveyAttempt;
  }

  @Output()
  surveyReviewEnd: EventEmitter<void> = new EventEmitter<void>();

  _surveyAttempt?: SurveyAttemptDto;
  visible = true;
  loading = false;
  questionsMap: {[id: number]: SurveyQuestionDto} = {};

  private correctString = 'Верно';
  private incorrectString = 'Неверно';

  constructor(private messageService: MessageService,
              private surveyUtilService: SurveyUtilService,
              private surveyQuestionService: SurveyQuestionService,
              private surveyAttemptService: SurveyAttemptService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      if (this.surveyAttempt?.finishDate == null) {
        this.messageService.add(
          {
            severity: 'error',
            summary: 'Ошибка',
            detail: 'Попытка прохождения тестирования ещё не завершена',
            key: MessageServiceKey.OK,
            sticky: true
          }
        );
        this.surveyReviewEnd.emit();
        return;
      }
      if (this.surveyAttempt?.id == null) {
        this.messageService.add(
          {
            severity: 'error',
            summary: 'Ошибка',
            detail: 'Не найдена попытка прохождения тестирования',
            key: MessageServiceKey.OK,
            sticky: true
          }
        );
        this.surveyReviewEnd.emit();
        return;
      }
      if (this.surveyAttempt?.surveyId == null) {
        this.messageService.add(
          {
            severity: 'error',
            summary: 'Ошибка',
            detail: 'Тестирование не найдено',
            key: MessageServiceKey.OK,
            sticky: true
          }
        );
        this.surveyReviewEnd.emit();
        return;
      }
      this.surveyAttempt!.answers = await lastValueFrom(this.surveyAttemptService.getAttemptAnswers(this.surveyAttempt.surveyId, this.surveyAttempt.id!));
      const questions = await lastValueFrom(this.surveyQuestionService.getSurveyQuestionsWithAnswers(this.surveyAttempt.surveyId));
      this.questionsMap = questions.reduce<{[id: number]: SurveyQuestionDto}>((prev, cur, i, arr) => {
        prev[cur.id!] = cur;
        return prev;
      }, {});
      const surveyJson = {
        pages: questions.map(question => ({
          name: 'page' + question.orderNumber,
          elements: [
            this.surveyUtilService.toSurveyJson(question, {withCorrectAnswers: true})
          ]
        }))
      };
      const surveyModel = new Model(surveyJson);

      surveyModel.applyTheme(this.surveyUtilService.getThemeJson());
      surveyModel.locale = 'ru';
      surveyModel.showCompletedPage = false;
      surveyModel.isSinglePage = true;
      surveyModel.mode = "display";
      surveyModel.progressBarShowPageNumbers = false;
      surveyModel.onProgressText.add((survey, options) => options.text = `Вопрос ${survey.currentPageNo + 1} из ${survey.pageCount}`);
      surveyModel.onUpdatePanelCssClasses.add((survey, options) => {
      });
      if (this.surveyAttempt?.answers != null) {
        surveyModel.data = this.surveyUtilService.constructSurveyJsData(this.surveyAttempt.answers);
      }
      SurveyNG.render('surveyJsQuestionReview', {model: surveyModel});
      surveyModel.onIsAnswerCorrect.add((survey, options) => {
        const question = options.question;
        if (this.questionsMap[Number(question?.name)]?.type === SurveyQuestionType.STRING && Array.isArray(question?.correctAnswer)) {
          options.result = (question.correctAnswer as any[])?.some(answer => {
            if ((typeof answer === 'string' || answer instanceof String) && (typeof question.value === 'string' || question.value instanceof String)) {
              return (answer as string).trim().localeCompare((question.value as string), undefined, {sensitivity: 'accent'}) === 0;
            } else {
              return false;
            }
          });
        }
      });
      surveyModel.onGetQuestionTitle.add((survey, options) => {
        options.title = this.getQuestionTitleWithCorrectString(options.question, options.title);
      });
      surveyModel.onTextMarkdown.add((survey, options) => {
        const html = this.markdownCorrectString(options.text);
        if (html != null) {
          options.html = html;
        }
      });
    } catch (e) {
      this.surveyReviewEnd.emit();
    } finally {
      this.loading = false;
    }
  }

  markdownCorrectString (text: string): string | undefined {
    const correctStringIndex = text.indexOf(this.correctString);
    const incorrectStringIndex = text.indexOf(this.incorrectString);
    if (correctStringIndex >= 0) {
      return text.substring(0, correctStringIndex) + `<span class="surveyjs-correct-answer">${this.correctString}</span>`
    } else if (incorrectStringIndex >=0) {
      return text.substring(0, incorrectStringIndex) + `<span class="surveyjs-incorrect-answer">${this.incorrectString}</span>`
    } else {
      return undefined;
    }
  }

  getQuestionTitleWithCorrectString (question: Question, title: string): string {
    const isCorrect = question.isAnswerCorrect();
    if (isCorrect == null) {
      return title;
    }
    return question.title + ' ' + (isCorrect ? this.correctString : this.incorrectString);
  }
}
