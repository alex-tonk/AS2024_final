import {Component, OnDestroy, OnInit} from '@angular/core';
import {PrimeTemplate} from 'primeng/api';
import {TabViewModule} from 'primeng/tabview';
import {VideoLessonsComponent} from '../student-cabinet/samples/video-lessons/video-lessons.component';
import {CardModule} from 'primeng/card';
import {RouterLink} from '@angular/router';
import {StudyGroupCardComponent} from './study-group-card/study-group-card.component';
import {NgForOf, NgIf} from '@angular/common';
import {DataViewModule} from 'primeng/dataview';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {OnlineLessonsComponent} from '../student-cabinet/samples/online-lessons/online-lessons.component';
import {
  PresentationLessonsComponent
} from '../student-cabinet/samples/presentation-lessons/presentation-lessons.component';
import {SplitterModule} from 'primeng/splitter';
import {CoursePanelComponent, CoursePanelMode} from '../../forms/course-panel/course-panel.component';
import {AttemptComponent, AttemptListMode} from '../../attempt/attempt.component';
import {DropdownModule} from 'primeng/dropdown';
import {ProgressBarModule} from 'primeng/progressbar';
import {TagModule} from 'primeng/tag';
import {TooltipModule} from 'primeng/tooltip';
import {TopicDto} from '../../../gen/atom2024backend-dto';
import {TopicService} from '../../../gen/atom2024backend-controllers';
import {UserService} from '../../../services/user.service';
import {lastValueFrom} from 'rxjs';
import {setTimeout} from 'core-js';
import {Button} from 'primeng/button';
import {ChatType} from '../../../gen/entities-enums';
import {DialogModule} from 'primeng/dialog';
import {ListboxModule} from 'primeng/listbox';
import {UserDto} from '../../../models/UserDto';
import FileSaver from 'file-saver';
import {ReportService} from '../../../services/report.service';

@Component({
  selector: 'app-tutor-cabinet',
  standalone: true,
  imports: [
    OnlineLessonsComponent,
    PresentationLessonsComponent,
    PrimeTemplate,
    TabViewModule,
    VideoLessonsComponent,
    CardModule,
    RouterLink,
    StudyGroupCardComponent,
    NgForOf,
    DataViewModule,
    InputTextModule,
    FormsModule,
    SplitterModule,
    CoursePanelComponent,
    AttemptComponent,
    DropdownModule,
    ProgressBarModule,
    TagModule,
    TooltipModule,
    Button,
    DialogModule,
    ListboxModule,
    NgIf
  ],
  templateUrl: './tutor-cabinet.component.html',
  styleUrl: './tutor-cabinet.component.css'
})
export class TutorCabinetComponent implements OnInit, OnDestroy {
  loading = false;
  activeIndex = 0;

  systemTabsCount = 2;
  topics: TopicDto[] = [];
  openedTopics: { value: TopicDto, title: string }[] = [];

  filterValue: string;
  filterOptions = [
    {value: 'all', label: 'По всему содержимому'},
    {value: 'lessons', label: 'По содержанию уроков'},
    {value: 'tasks', label: 'По содержанию заданий'}
  ];
  filterOption = 'all';
  topicRefreshInterval: number;

  get filteredCourses() {
    if (this.filterValue) {
      if (this.filterOption === 'lessons') {
        return this.topics
          .map(t => t)
          .filter(g => JSON.stringify(g.lessons).toLowerCase().includes(this.filterValue.toLowerCase()));
      }
      if (this.filterOption === 'tasks') {
        return this.topics
          .map(t => t)
          .filter(g => JSON.stringify(g.lessons?.flatMap(l => l.tasks)).toLowerCase().includes(this.filterValue.toLowerCase()));
      }
      return this.topics.filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
    } else {
      return this.topics;
    }
  }

  constructor(private topicService: TopicService,
              protected userService: UserService,
              private reportService: ReportService) {
  }

  async ngOnInit() {
    await this.init();

    this.topicRefreshInterval = setInterval(
      async () => {
        const topics = await lastValueFrom(this.topicService.getTopics());
        let topicsById: { [key: number]: TopicDto } = {};
        topics.reduce((prev, cur) => {
          prev[cur.id!] = cur;
          return prev;
        }, topicsById);
        this.topics.forEach(t => {
          const newTopic = topicsById[t.id!];
          if (newTopic != null) {
            t.taskPassedCount = newTopic.taskPassedCount;
          }
        })
      }, 5000);
  }

  ngOnDestroy() {
    clearInterval(this.topicRefreshInterval);
  }

  async init() {
    this.loading = true;
    try {
      this.topics = await lastValueFrom(this.topicService.getTopics());
    } finally {
      this.loading = false;
    }
  }

  openTopic(course: TopicDto) {
    const idx = this.openedTopics.findIndex(g => g.value.id === course.id);
    if (idx > -1) {
      this.activeIndex = idx + this.systemTabsCount;
    } else {
      this.openedTopics.push({value: course, title: course.title!});
      setTimeout(() => {
        this.activeIndex = this.openedTopics.length + this.systemTabsCount - 1;
      });
    }
  }

  closeCourse(index: number) {
    this.activeIndex = 0;
    this.openedTopics.splice(index - this.systemTabsCount, 1);
  }

  protected readonly AttemptListMode = AttemptListMode;
  protected readonly CoursePanelMode = CoursePanelMode;

  protected readonly ChatType = ChatType;

  printTopicId?: number;
  printDiplomaDialogVisible = false;
  availableForPrintUsers: UserDto[] = [];
  printUser?: UserDto;

  async confirmPrintDiplomaFull(event: MouseEvent, topicId: number) {
    this.loading = true;
    try {
      console.log('wtf');
      event.stopPropagation();
      event.preventDefault();
      event.stopImmediatePropagation();
      this.availableForPrintUsers = await lastValueFrom(this.topicService.getUsersWithFinishedTopic(topicId));
      this.printDiplomaDialogVisible = true;
      this.printTopicId = topicId;
    } finally {
      this.loading = false;
    }
  }

  async printDiplomaFull() {
    this.loading = true;
    try {
      let blob = await lastValueFrom(this.reportService.printDiplomaSpecFull(this.printTopicId!, this.printUser!.id));
      FileSaver.saveAs(blob, 'Диплом с приложением.pdf');
    } finally {
      this.loading = false;
    }
  }

  onDiplomaPrintFinished() {
    this.printDiplomaDialogVisible = false;
    this.printTopicId = undefined;
    this.availableForPrintUsers = [];
    this.printUser = undefined;
  }
}
