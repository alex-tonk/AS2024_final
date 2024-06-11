import {Injectable} from '@angular/core';
import {SurveyAttemptAnswerDto, SurveyQuestionDto} from '../gen/survey-dto';
import {SurveyQuestionType} from '../gen/survey-enums';
import {
  CheckboxSurveyQuestionAnswerMeta,
  NumberSurveyQuestionAnswerMeta,
  PredefinedAnswerQuestionMeta,
  RadioButtonSurveyQuestionAnswerMeta,
  RankingSurveyQuestionAnswerMeta,
  StringSurveyQuestionAnswerMeta,
  StringSurveyQuestionCorrectAnswerMeta
} from '../models/SurveyQuestionMeta';

@Injectable({
  providedIn: 'root'
})
export class SurveyUtilService {
  toSurveyJson(question: SurveyQuestionDto, options: {withCorrectAnswers?: boolean, shuffleOptions?: boolean} = {}): any {
    const calculatedOptions: {withCorrectAnswers?: boolean, shuffleOptions?: boolean} = Object.assign({withCorrectAnswers: false, shuffleOptions: false}, options);
    const result: any = {
      type: 'panel',
      name: 'panel' + (question.id ?? 0),
      elements: [
        {
          name: (question.id ?? 0).toString(),
          title: question.wording,
          description: question.comment
        }
      ]
    };
    switch (question.type as SurveyQuestionType) {
      case SurveyQuestionType.STRING:
        result.elements[0].type = 'text';
        if (calculatedOptions.withCorrectAnswers) {
          result.elements[0].correctAnswer = (question.correctAnswerMeta as StringSurveyQuestionCorrectAnswerMeta)?.correctAnswers;
        }
        break;
      case SurveyQuestionType.NUMBER:
        result.elements[0].type = 'text';
        result.elements[0].inputType = 'number';
        if (calculatedOptions.withCorrectAnswers) {
          result.elements[0].correctAnswer = (question.correctAnswerMeta as NumberSurveyQuestionAnswerMeta)?.answer;
        }
        break;
      case SurveyQuestionType.RADIO_BUTTON: {
        result.elements[0].type = 'radiogroup';
        result.elements[0].showNoneItem = false;
        result.elements[0].showOtherItem = false;
        result.elements[0].colCount = 1;
        let choices = ((question.meta as PredefinedAnswerQuestionMeta).answers ?? []).map(a => ({
          value: a.id,
          text: a.value ?? ' '
        }));
        if (calculatedOptions.shuffleOptions) {
          this.shuffleArray(choices);
        }
        result.elements[0].choices = choices;
        if (calculatedOptions.withCorrectAnswers) {
          result.elements[0].correctAnswer = (question.correctAnswerMeta as RadioButtonSurveyQuestionAnswerMeta)?.answerId;
        }
        break;
      }
      case SurveyQuestionType.CHECKBOX: {
        result.elements[0].type = 'checkbox';
        result.elements[0].showNoneItem = false;
        result.elements[0].showOtherItem = false;
        result.elements[0].colCount = 1;
        let choices = ((question.meta as PredefinedAnswerQuestionMeta).answers ?? []).map(a => ({
          value: a.id,
          text: a.value ?? ' '
        }));
        if (calculatedOptions.shuffleOptions) {
          this.shuffleArray(choices);
        }
        result.elements[0].choices = choices;
        if (calculatedOptions.withCorrectAnswers) {
          result.elements[0].correctAnswer = (question.correctAnswerMeta as CheckboxSurveyQuestionAnswerMeta)?.answerIds;
        }
        break;
      }
      case SurveyQuestionType.RANKING: {
        result.elements[0].type = 'ranking';
        let choices = ((question.meta as PredefinedAnswerQuestionMeta).answers ?? []).map(a => ({
          value: a.id,
          text: a.value ?? ' '
        }));
        if (calculatedOptions.shuffleOptions) {
          this.shuffleArray(choices);
        }
        result.elements[0].choices = choices;
        if (calculatedOptions.withCorrectAnswers) {
          result.elements[0].correctAnswer = (question.correctAnswerMeta as RankingSurveyQuestionAnswerMeta)?.answerIdsOrdered;
        }
        break;
      }
    }
    return result;
  }

  public surveyQuestionInvalidReason(question: SurveyQuestionDto): string | null {
    if ((question?.wording ?? '').length == 0) {
      return 'Не задана формулировка вопроса';
    }
    switch (question.type as SurveyQuestionType) {
      case SurveyQuestionType.STRING:
        const stringCorrectAnswerMeta = question.correctAnswerMeta as StringSurveyQuestionCorrectAnswerMeta;
        if ((stringCorrectAnswerMeta?.correctAnswers ?? []).length == 0) {
          return 'Не задано ни одного правильного ответа';
        }
        break;
      case SurveyQuestionType.NUMBER:
        const numberCorrectAnswerMeta = question.correctAnswerMeta as NumberSurveyQuestionAnswerMeta;
        if (numberCorrectAnswerMeta?.answer == null) {
          return 'Не задан правильный ответ';
        }
        break;
      case SurveyQuestionType.RADIO_BUTTON:
      case SurveyQuestionType.CHECKBOX:
      case SurveyQuestionType.RANKING:
        const questionMeta = question.meta as PredefinedAnswerQuestionMeta;
        if ((questionMeta?.answers ?? []).length == 0) {
          return 'Не задано ни одного варианта ответа';
        }
        if ((questionMeta?.answers ?? []).some(a => (a?.value ?? '').length == 0)) {
          return 'У варианта ответа отсутствует формулировка';
        }

        switch (question.type as SurveyQuestionType) {
          case SurveyQuestionType.RADIO_BUTTON:
            const radioButtonCorrectAnswerMeta = question.correctAnswerMeta as RadioButtonSurveyQuestionAnswerMeta;
            if (radioButtonCorrectAnswerMeta?.answerId == null) {
              return 'Не задан правильный ответ';
            } else if (!questionMeta.answers?.some(a => a.id === radioButtonCorrectAnswerMeta.answerId)) {
              return 'Правильный ответ не содержится в вариантах ответа';
            }
            break;
          case SurveyQuestionType.CHECKBOX: {
            const checkboxCorrectAnswerMeta = question.correctAnswerMeta as CheckboxSurveyQuestionAnswerMeta;
            if ((checkboxCorrectAnswerMeta?.answerIds ?? []).length == 0) {
              return 'Не задано ни одного правильного ответа'
            }
            const answerIds: number[] = (questionMeta?.answers ?? []).map(a => a.id!);
            if ((checkboxCorrectAnswerMeta?.answerIds ?? []).filter(id => !answerIds.includes(id)).length > 0) {
              return 'Правильный ответ не содержится в вариантах ответа';
            }
            break;
          }
          case SurveyQuestionType.RANKING: {
            const rankingCorrectAnswerMeta = question.correctAnswerMeta as RankingSurveyQuestionAnswerMeta;
            const answerIds: number[] = (questionMeta?.answers ?? []).map(a => a.id!);
            if ([
              ...(rankingCorrectAnswerMeta?.answerIdsOrdered ?? []).filter(id => !answerIds.includes(id)),
              ...(answerIds.filter(id => !((rankingCorrectAnswerMeta?.answerIdsOrdered ?? []).includes(id)))),
            ].length > 0) {
              return 'Некорректный правильный порядок';
            }
            break;
          }
        }
        break;
    }
    return null;
  }

  public constructSurveyAttemptAnswer(surveyAttemptId: number, question: SurveyQuestionDto, value: any): SurveyAttemptAnswerDto {
    const answer = new SurveyAttemptAnswerDto();
    answer.surveyAttemptId = surveyAttemptId;
    answer.questionId = question.id;
    let meta = null;
    switch (question.type as SurveyQuestionType) {
      case SurveyQuestionType.STRING:
        answer.type = SurveyQuestionType.STRING;
        meta = new StringSurveyQuestionAnswerMeta();
        meta.answer = value;
        break;
      case SurveyQuestionType.NUMBER:
        answer.type = SurveyQuestionType.NUMBER;
        meta = new NumberSurveyQuestionAnswerMeta();
        meta.answer = value;
        break;
      case SurveyQuestionType.RADIO_BUTTON:
        answer.type = SurveyQuestionType.RADIO_BUTTON;
        meta = new RadioButtonSurveyQuestionAnswerMeta();
        meta.answerId = value;
        break;
      case SurveyQuestionType.CHECKBOX:
        answer.type = SurveyQuestionType.CHECKBOX;
        meta = new CheckboxSurveyQuestionAnswerMeta();
        meta.answerIds = value;
        break;
      case SurveyQuestionType.RANKING:
        answer.type = SurveyQuestionType.RANKING;
        meta = new RankingSurveyQuestionAnswerMeta();
        meta.answerIdsOrdered = value;
        break;
    }
    answer.answer = meta;
    return answer;
  }

  public constructSurveyJsData(answers: SurveyAttemptAnswerDto[]): { [key: string]: any } {
    const res: { [key: string]: any } = {};
    answers.forEach(answer => {
      switch (answer.type as SurveyQuestionType) {
        case SurveyQuestionType.STRING:
          res[String(answer.questionId)] = (answer.answer as StringSurveyQuestionAnswerMeta)?.answer;
          break;
        case SurveyQuestionType.NUMBER:
          res[String(answer.questionId)] = (answer.answer as NumberSurveyQuestionAnswerMeta)?.answer;
          break;
        case SurveyQuestionType.RADIO_BUTTON:
          res[String(answer.questionId)] = (answer.answer as RadioButtonSurveyQuestionAnswerMeta)?.answerId;
          break;
        case SurveyQuestionType.CHECKBOX:
          res[String(answer.questionId)] = (answer.answer as CheckboxSurveyQuestionAnswerMeta)?.answerIds;
          break;
        case SurveyQuestionType.RANKING:
          res[String(answer.questionId)] = (answer.answer as RankingSurveyQuestionAnswerMeta)?.answerIdsOrdered;
          break;
      }
    });
    return res;
  }

  getThemeJson(): any {
    return {
      cssVariables: {
        '--primary': '#0077ce',
        '--primary-light': '#79cdf4',
        '--sjs-primary-backcolor-light': '#79cdf4',
        '--sjs-primary-backcolor': '#0077ce',
        '--sjs-primary-backcolor-dark': 'rgba(0, 113, 194, 0.66)',
        '--sjs-general-backcolor-dim': 'rgb(249, 250, 251)'
      }
    }
  }

  private shuffleArray<T>(array: T[]) {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      const temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
  }
}
