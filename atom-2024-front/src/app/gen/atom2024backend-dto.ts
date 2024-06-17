
export class LessonDto {
  author?: string;
  code?: string;
  content?: string;
  id?: number;
  tasks?: TaskDto[];
  title?: string;
  traits?: TraitDto[];
}

export class TaskDto {
  code?: string;
  content?: string;
  difficulty?: number;
  id?: number;
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

