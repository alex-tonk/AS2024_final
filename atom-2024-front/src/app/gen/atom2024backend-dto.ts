import {UserDto} from '../models/UserDto';
import {AttemptStatus, Mark} from './atom2024backend-enums';

export class AttemptCheckResultDto {
  features?: FeatureDto[];
  id?: number;
  isAutomatic?: boolean;
  x1?: number;
  x2?: number;
  y1?: number;
  y2?: number;
}

export class AttemptDto {
  autoCheckResults?: AttemptCheckResultDto[];
  autoMark?: Mark;
  endDate?: Date;
  files?: AttemptFileDto[];
  id?: number;
  isLastAttempt?: boolean;
  isNewTryAllowed?: boolean;
  lesson?: LessonDto;
  startDate?: Date;
  status?: AttemptStatus;
  task?: TaskDto;
  topic?: TopicDto;
  tutorCheckResults?: AttemptCheckResultDto[];
  tutorComment?: string;
  tutorMark?: Mark;
  user?: UserDto;
}

export class AttemptFileDto {
  comment?: string;
  fileId?: number;
  fileName?: string;
  id?: number;
}

export class FeatureDto {
  code?: string;
  id?: number;
  name?: string;
}

export class LessonDto {
  author?: string;
  code?: string;
  content?: string;
  id?: number;
  supplements?: SupplementDto[];
  tasks?: TaskDto[];
  title?: string;
  traits?: TraitDto[];
}

export class SupplementDto {
  fileId?: number;
  fileName?: string;
  supplementId?: number;
  title?: string;
}

export class TaskDto {
  averageTime?: number;
  code?: string;
  content?: string;
  difficulty?: number;
  difficultyScore?: number;
  id?: number;
  lastAttempt?: AttemptDto;
  numOfAttempts?: number;
  supplements?: SupplementDto[];
  time?: number;
  title?: string;
}

export class TopicDto {
  code?: string;
  description?: string;
  id?: number;
  lessonsCount?: number;
  title?: string;
  traits?: TraitDto[];
}

export class TraitDto {
  code?: string;
  description?: string;
  id?: number;
  name?: string;
}

