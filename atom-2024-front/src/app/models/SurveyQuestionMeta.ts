export class NumberSurveyQuestionAnswerMeta {
  answer?: number;
}

export class RadioButtonSurveyQuestionAnswerMeta {
  answerId?: number;
}

export class CheckboxSurveyQuestionAnswerMeta {
  answerIds?: number[];
}

export class RankingSurveyQuestionAnswerMeta {
  answerIdsOrdered?: number[];
}

export class StringSurveyQuestionAnswerMeta {
  answer?: string;
}

export class StringSurveyQuestionCorrectAnswerMeta {
  correctAnswers?: string[];
}

export class PredefinedAnswerMeta {
  id?: number;
  value?: string;
}

export class PredefinedAnswerQuestionMeta {
  answers?: PredefinedAnswerMeta[];
}
