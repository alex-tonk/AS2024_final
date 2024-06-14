import {UserDto} from '../models/UserDto';
import {SurveyQuestionType} from './survey-enums';

export class SurveyAttemptAnswerDto {
  answer?: any;
  id?: number;
  questionId?: number;
  surveyAttemptId?: number;
  type?: SurveyQuestionType;
}

export class SurveyAttemptDto {
  answers?: SurveyAttemptAnswerDto[];
  beginDate?: Date;
  correctAnswerCount?: number;
  finishDate?: Date;
  id?: number;
  lastAttempt?: boolean;
  surveyId?: number;
  user?: UserDto;
}

export class SurveyDto {
  id?: number;
  lastSurveyAttempt?: SurveyAttemptDto;
  name?: string;
  questionCount?: number;
  questions?: SurveyQuestionDto[];
  timeLimitMinutes?: number;
}

export class SurveyQuestionDto {
  comment?: string;
  correctAnswerMeta?: any;
  fileId?: number;
  fileName?: string;
  id?: number;
  meta?: any;
  orderNumber?: number;
  type?: SurveyQuestionType;
  wording?: string;
}

