import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
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
import {AttemptDto, AttemptFileDto, FeatureDto, LessonDto, TaskDto, TopicDto} from '../../../gen/atom2024backend-dto';
import {UserDto} from '../../../models/UserDto';
import {lastValueFrom} from 'rxjs';
import {AttemptService, FeatureService, TopicService} from '../../../gen/atom2024backend-controllers';
import {LectureViewComponent} from '../../pages/student-cabinet/lecture-view/lecture-view.component';
import {TaskAttemptComponent} from '../../pages/student-cabinet/task-attempt/task-attempt.component';
import {AttemptStatus} from '../../../gen/atom2024backend-enums';
import {KnobModule} from 'primeng/knob';
import {UserService} from '../../../services/user.service';
import {setInterval} from 'core-js';
import {TagModule} from 'primeng/tag';
import {TagStyleService} from '../../../services/tag-style-service';
import {DialogModule} from 'primeng/dialog';
import {DropdownModule} from 'primeng/dropdown';
import {AttemptComponent, AttemptListMode} from '../../attempt/attempt.component';
import {MessageService} from 'primeng/api';
import {
  ImageWithFeedbackViewerComponent
} from '../../common/image-with-feedback-viewer/image-with-feedback-viewer.component';
import {TabViewModule} from 'primeng/tabview';


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
    TaskAttemptComponent,
    KnobModule,
    TagModule,
    DialogModule,
    DropdownModule,
    AttemptComponent,
    ImageWithFeedbackViewerComponent,
    TabViewModule
  ],
  templateUrl: './course-panel.component.html',
  styleUrl: './course-panel.component.css'
})
export class CoursePanelComponent implements OnInit, OnDestroy {
  protected readonly AttemptStatus = AttemptStatus;

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
  taskTimersInterval: number;
  backgroundRefreshInterval: number;

  remainingTimeByAttemptId: { [k: string]: string } = {};

  get filteredLessons() {
    if (this.filterValue) {
      return this.lessons.filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
    } else {
      return this.lessons;
    }
  }

  get header(): string {
    return `Учебная тема: ${this.topic.title!}`
  }

  constructor(
    private topicService: TopicService,
    private userService: UserService,
    protected tagStyleService: TagStyleService,
    private messageService: MessageService,
    private featureService: FeatureService,
    private attemptService: AttemptService
  ) {
  }

  async ngOnInit() {
    await this.reload();
    this.features = await lastValueFrom(this.featureService.getFeatures());
    this.backgroundRefreshInterval = setInterval(
      async () => {
        let lessons;
        if (this.mode === CoursePanelMode.STUDENT) {
          lessons = await lastValueFrom(this.topicService.getTopicLessonsWithLastAttempts(this.topic.id!));
        } else {
          lessons = await lastValueFrom(this.topicService.getTopicLessons(this.topic.id!));
        }
        let attemptsByTaskId: { [key: string]: AttemptDto | undefined } = {};
        attemptsByTaskId = lessons.flatMap(l => (l.tasks ?? []).map(t => ({
          lessonId: l.id!,
          taskId: t.id!,
          attempt: t.lastAttempt
        })))
          .reduce((prev, cur) => {
            if (cur != null) {
              prev[cur.lessonId + '/' + cur.taskId] = cur.attempt;
            }
            return prev;
          }, attemptsByTaskId);

        this.lessons.flatMap(l => (l.tasks ?? []).map(t => ({lessonId: l.id!, task: t})))
          .forEach(key => key.task.lastAttempt = attemptsByTaskId[key.lessonId + '/' + key.task.id!]);
      }, 5000);
  }

  ngOnDestroy() {
    clearInterval(this.taskTimersInterval);
    clearInterval(this.backgroundRefreshInterval);
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
      this.tasksByLesson[lesson.id!].push({title: 'Учебный материал', lesson: lesson, type: 'lecture'});
      (lesson.tasks ?? []).forEach(task => {
        this.tasksByLesson[lesson.id!].push({title: task.title, lesson: lesson, task: task, type: 'task'});
      })
    });
    this.restartTimeCalculation();
  }

  private restartTimeCalculation() {
    clearInterval(this.taskTimersInterval);
    this.remainingTimeByAttemptId = {};
    this.taskTimersInterval = setInterval(() => {
      for (let lesson of this.lessons) {
        lesson.tasks?.filter(task => task.lastAttempt != null).map(task => task.lastAttempt)
          .forEach(attempt => {
            this.remainingTimeByAttemptId[attempt?.id?.toString() ?? ''] = this.calcRemainingTime(attempt!);
          });
      }
    }, 1000);
  }

  openLectureOrTask(task: { title?: string, lesson: LessonDto, task?: TaskDto, type: 'task' | 'lecture' }) {
    if (task.type === 'lecture') {
      this.lessonForLectureView = task.lesson;
      this.lectureViewVisible = true;
      this.saveOpenHistory(task.lesson.id!)
    } else if (task.type === 'task') {
      if (this.mode == CoursePanelMode.TUTOR) {
        this.tutorFilterData = {topicId: this.topic!.id!, lessonId: task.lesson.id!, taskId: task.task!.id!}
        this.studentAttemptsVisible = true;
      } else {
        if (task.task?.lastAttempt == null || task.task?.lastAttempt?.status === AttemptStatus.IN_PROGRESS || !!task.task?.lastAttempt?.isNewTryAllowed ) {
          this.taskAttemptFormData = {topic: this.topic, lesson: task.lesson, task: task.task!};
          this.taskAttemptVisible = true;
        } else {
          this.messageService.add({severity: 'warn', summary: 'Внимание', detail: 'Повторное выполнение задания не разрешено'});
        }
      }
    }
  }

  saveOpenHistory(lessonId: number) {
    const key = 'openedLessons' + this.userService.user?.id;
    const val = localStorage.getItem(key);
    if (val) {
      const arr = JSON.parse(val);
      if (!arr.includes(lessonId)) {
        arr.push(lessonId);
      }
      localStorage.setItem(key, JSON.stringify(arr))
    } else {
      localStorage.setItem(key, JSON.stringify([lessonId]))
    }
  }

  isWasOpened(lessonId: number): boolean {
    const key = 'openedLessons' + this.userService.user?.id;
    const val = localStorage.getItem(key);
    if (val) {
      const arr = JSON.parse(val)
      if (arr.includes(lessonId)) {
        return true;
      }
    }
    return false;
  }

  onLectureViewEnd() {
    this.lectureViewVisible = false;
    this.lessonForLectureView = undefined;
  }

  async onTaskAttemptClose(needReload: boolean) {
    this.taskAttemptVisible = false;
    this.taskAttemptFormData = undefined;
  }

  getDifficultLabel(val: number) {
    if (!val) {
      return 'Сложность задания еще не определена';
    }
    if (val < 3) {
      return 'Простое задание';
    }
    if (val < 5) {
      return 'Среднее задание';
    }
    return 'Сложное задание';
  }

  getDifficultColor(val: number) {
    if (!val || val < 3) {
      return '#0077ce';
    }
    if (val < 5) {
      return '#FEBB02';
    }
    return 'rgba(227, 77, 77, 0.66)';
  }

  private calcRemainingTime(attempt: AttemptDto) {
    let diffSecs = Math.round(attempt?.task?.time! * 60 - (new Date().getTime() - attempt?.startDate?.getTime()!) / 1000);
    if (diffSecs < 0) diffSecs = 0;
    const minutes = Math.floor(diffSecs / 60);
    const seconds = diffSecs - minutes * 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toFixed(0).padStart(2, '0')}`;
  }

  protected readonly CoursePanelMode = CoursePanelMode;
  protected readonly AttemptListMode = AttemptListMode;
  studentAttemptsVisible = false;
  tutorFilterData: { topicId: number | null; lessonId: number | null; taskId: number | null } | null;

  onStudentsAttemptsClose() {
    this.studentAttemptsVisible = false;
    this.tutorFilterData = null;
  }

  features: FeatureDto[] = [];
  activeIndex = 0;
  checkingDialogVisible = false;
  checkingAttempt?: AttemptDto;
  checkingAttemptFiles: AttemptFileDto[] = [];

  async viewAttempt(attemptId: number) {
    this.loading = true;
    try {
      this.checkingAttempt = await lastValueFrom(this.attemptService.getAttempt(attemptId));
      this.checkingAttemptFiles = this.checkingAttempt.files ?? [];
      this.checkingDialogVisible = true;
    } finally {
      this.loading = false;
    }
  }

  onAttemptViewClose() {
    this.activeIndex = 0
    this.checkingDialogVisible = false;
    this.checkingAttempt = undefined;
    this.checkingAttemptFiles = [];
  }
}
