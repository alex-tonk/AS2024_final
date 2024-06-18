import {Component, OnInit} from '@angular/core';
import {TabViewModule} from 'primeng/tabview';
import {UserListComponent} from '../admin-panel/user-list/user-list.component';
import {VideoPlayerComponent} from '../../common/video-player/video-player.component';
import {VideoLessonsComponent} from './samples/video-lessons/video-lessons.component';
import {PresentationLessonsComponent} from './samples/presentation-lessons/presentation-lessons.component';
import {OnlineLessonsComponent} from './samples/online-lessons/online-lessons.component';
import {SplitterModule} from 'primeng/splitter';
import {lastValueFrom} from 'rxjs';
import {setTimeout} from 'core-js';
import {DataViewModule} from 'primeng/dataview';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {DatePipe, NgForOf, NgIf, NgStyle} from '@angular/common';
import {CardModule} from 'primeng/card';
import {StudyGroupCardComponent} from '../tutor-cabinet/study-group-card/study-group-card.component';
import {CoursePanelComponent, CoursePanelMode} from '../../forms/course-panel/course-panel.component';
import {CalendarModule} from 'primeng/calendar';
import {TooltipModule} from 'primeng/tooltip';
import {ProgressBarModule} from 'primeng/progressbar';
import {TopicDto} from '../../../gen/atom2024backend-dto';
import {TopicService} from '../../../gen/atom2024backend-controllers';
import {TagModule} from 'primeng/tag';
import {UserService} from '../../../services/user.service';
import {DropdownModule} from 'primeng/dropdown';

@Component({
  selector: 'app-student-cabinet',
  standalone: true,
  imports: [
    TabViewModule,
    UserListComponent,
    VideoPlayerComponent,
    VideoLessonsComponent,
    PresentationLessonsComponent,
    OnlineLessonsComponent,
    SplitterModule,
    DataViewModule,
    InputTextModule,
    FormsModule,
    NgForOf,
    CardModule,
    StudyGroupCardComponent,
    CoursePanelComponent,
    CalendarModule,
    NgStyle,
    DatePipe,
    TooltipModule,
    NgIf,
    ProgressBarModule,
    TagModule,
    DropdownModule
  ],
  templateUrl: './student-cabinet.component.html',
  styleUrl: './student-cabinet.component.css'
})
export class StudentCabinetComponent implements OnInit {
  loading = false;
  activeIndex = 0;

  systemTabsCount = 1;
  topics: TopicDto[] = [];
  openedTopics: { value: TopicDto, title: string }[] = [];

  filterValue: string;
  filterOptions = [
    {value: 'all', label: 'По всему содержимому'},
    {value: 'lessons', label: 'По содержанию уроков'},
    {value: 'tasks', label: 'По содержанию заданий'}
  ];
  filterOption = 'all';

  get filteredCourses() {
    if (this.filterValue) {
      if (this.filterOption === 'lessons') {
        // TODO Воткнуть .map
        return this.topics
          .map(t => t)
          .filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
      }
      if (this.filterOption === 'tasks') {
        // TODO Воткнуть .map
        return this.topics
          .map(t => t)
          .filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
      }
      return this.topics.filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
    } else {
      return this.topics;
    }
  }

  constructor(private topicService: TopicService,
              protected userService: UserService) {
  }

  async ngOnInit() {
    await this.init();
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

  protected readonly CoursePanelMode = CoursePanelMode;
}
