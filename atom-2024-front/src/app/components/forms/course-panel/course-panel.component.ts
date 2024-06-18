import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {VideoLessonsComponent} from '../../pages/student-cabinet/samples/video-lessons/video-lessons.component';
import {AccordionModule} from 'primeng/accordion';
import {NgForOf, NgIf} from '@angular/common';
import {VideoPlayerComponent} from '../../common/video-player/video-player.component';
import {Button} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {TooltipModule} from 'primeng/tooltip';
import {FormsModule} from '@angular/forms';
import {PanelModule} from 'primeng/panel';
import {DataViewModule} from 'primeng/dataview';
import {CardModule} from 'primeng/card';
import {LessonDto, TaskDto, TopicDto} from '../../../gen/atom2024backend-dto';
import {UserDto} from '../../../models/UserDto';
import {lastValueFrom} from 'rxjs';
import {TopicService} from '../../../gen/atom2024backend-controllers';
import {LectureViewComponent} from '../../pages/student-cabinet/lecture-view/lecture-view.component';
import {TaskAttemptComponent} from '../../pages/student-cabinet/task-attempt/task-attempt.component';
import { AttemptStatus } from '../../../gen/atom2024backend-enums';


export enum CoursePanelMode {
  STUDENT = 'STUDENT', TUTOR = 'TUTOR'
}

@Component({
  selector: 'app-course-panel',
  standalone: true,
  imports: [
    VideoLessonsComponent,
    AccordionModule,
    NgForOf,
    VideoPlayerComponent,
    Button,
    InputTextModule,
    NgIf,
    TooltipModule,
    FormsModule,
    PanelModule,
    DataViewModule,
    CardModule,
    LectureViewComponent,
    TaskAttemptComponent
  ],
  templateUrl: './course-panel.component.html',
  styleUrl: './course-panel.component.css'
})
export class CoursePanelComponent implements OnInit {
  @Input() mode: CoursePanelMode = CoursePanelMode.STUDENT;

  @Input() student: UserDto;
  @Input() topic: TopicDto;

  @Output() backToCourseList = new EventEmitter();

  lessons: LessonDto[] = [];
  tasksByLesson: {
    [key: number]: { title?: string, lesson: LessonDto, task?: TaskDto, type: 'task' | 'lecture' }[]
  } = {}
  loading = false;
  filterValue: string;

  lectureViewVisible = false;
  lessonForLectureView?: LessonDto;

  taskAttemptFormData?: { topic: TopicDto, lesson: LessonDto, task: TaskDto };
  taskAttemptVisible = false;

  get filteredLessons() {
    if (this.filterValue) {
      return this.lessons.filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
    } else {
      return this.lessons;
    }
  }

  get header(): string {
    switch (this.mode) {
      case CoursePanelMode.STUDENT:
        return `Курс: ${this.topic.title!}`
      case CoursePanelMode.TUTOR:
        return `${this.topic.title!} / ${this.student.fullName!}`
    }
  }

  constructor(private topicService: TopicService) {
  }

  async ngOnInit() {
    await this.reload();
  }

  private async reload() {
    if (this.mode === CoursePanelMode.STUDENT) {
      this.lessons = await lastValueFrom(this.topicService.getTopicLessonsWithLastAttempts(this.topic.id!));
    } else {
      this.lessons = await lastValueFrom(this.topicService.getTopicLessons(this.topic.id!));
    }
    this.tasksByLesson = {};
    this.lessons.forEach(lesson => {
      this.tasksByLesson[lesson.id!] = [];
      this.tasksByLesson[lesson.id!].push({title: 'Лекция', lesson: lesson, type: 'lecture'});
      (lesson.tasks ?? []).forEach(task => {
        this.tasksByLesson[lesson.id!].push({title: task.title, lesson: lesson, task: task, type: 'task'});
      })

    });
  }

  createModule() {
    alert('Создание нового модуля')
  }

  editModule(module: any, event: MouseEvent) {
    // Для предотвращения открытия аккардиона
    event.stopImmediatePropagation();
    alert('Редактирование модуля ' + module.name)
  }

  deleteModule(module: any, event: MouseEvent) {
    event.stopImmediatePropagation();
    alert('Удаление модуля ' + module.name)
  }

  protected readonly CoursePanelMode = CoursePanelMode;
  protected readonly AttemptStatus = AttemptStatus;

  openLectureOrTask(task: { title?: string, lesson: LessonDto, task?: TaskDto, type: 'task' | 'lecture' }) {
    if (task.type === 'lecture') {
      this.lessonForLectureView = task.lesson;
      this.lectureViewVisible = true;
    } else if (task.type === 'task') {
      this.taskAttemptFormData = {topic: this.topic, lesson: task.lesson, task: task.task!};
      this.taskAttemptVisible = true;
    }
  }

  onLectureViewEnd() {
    this.lectureViewVisible = false;
    this.lessonForLectureView = undefined;
  }

  async onTaskAttemptClose() {
    this.taskAttemptVisible = false;
    this.taskAttemptFormData = undefined;
    await this.reload();
  }
}
