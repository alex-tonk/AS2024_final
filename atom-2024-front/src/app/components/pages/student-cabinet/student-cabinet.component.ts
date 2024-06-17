import {Component, OnInit} from '@angular/core';
import {TabViewModule} from 'primeng/tabview';
import {UserListComponent} from '../admin-panel/user-list/user-list.component';
import {VideoPlayerComponent} from '../../common/video-player/video-player.component';
import {VideoLessonsComponent} from './samples/video-lessons/video-lessons.component';
import {PresentationLessonsComponent} from './samples/presentation-lessons/presentation-lessons.component';
import {OnlineLessonsComponent} from './samples/online-lessons/online-lessons.component';
import {SplitterModule} from 'primeng/splitter';
import {CourseDto, StudyGroupDto} from '../../../gen/atom2024backend-dto';
import {CourseService, StudyGroupService} from '../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {setTimeout} from 'core-js';
import {DataViewModule} from 'primeng/dataview';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {DatePipe, NgForOf, NgIf, NgStyle} from '@angular/common';
import {CardModule} from 'primeng/card';
import {StudyGroupCardComponent} from '../tutor-cabinet/study-group-card/study-group-card.component';
import {TopicPanelComponent, TopicPanelMode} from '../../forms/lesson-panel/topic-panel.component';
import {CalendarModule} from 'primeng/calendar';
import {TooltipModule} from 'primeng/tooltip';
import {ProgressBarModule} from 'primeng/progressbar';

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
    TopicPanelComponent,
    CalendarModule,
    NgStyle,
    DatePipe,
    TooltipModule,
    NgIf,
    ProgressBarModule
  ],
  templateUrl: './student-cabinet.component.html',
  styleUrl: './student-cabinet.component.css'
})
export class StudentCabinetComponent implements OnInit {
  loading = false;
  activeIndex = 0;

  systemTabsCount = 1;
  // TODO Курсы для ученика!!
  availableCourses: CourseDto[] = [];
  openedCourses: { value: CourseDto, title: string }[] = [];

  filterValue: string;
  selectedDate?: Date;

  get filteredCourses() {
    if (this.filterValue) {
      return this.availableCourses.filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
    } else {
      return this.availableCourses;
    }
  }

  constructor(private courseService: CourseService) {
  }

  ngOnInit() {
    this.init();
  }

  async init() {
    this.loading = true;
    try {
      this.availableCourses = await lastValueFrom(this.courseService.getCourses());
    } finally {
      this.loading = false;
    }
  }

  openCourse(course: CourseDto) {
    const idx = this.openedCourses.findIndex(g => g.value.id === course.id);
    if (idx > -1) {
      this.activeIndex = idx + this.systemTabsCount;
    } else {
      this.openedCourses.push({value: course, title: course.name!});
      setTimeout(() => {
        this.activeIndex = this.openedCourses.length + this.systemTabsCount - 1;
      });
    }
  }

  closeCourse(index: number) {
    this.activeIndex = 0;
    this.openedCourses.splice(index - this.systemTabsCount, 1);
  }

  random() {
    return Number((Math.random() * 100).toFixed(0));
  }



  protected readonly CoursePanelMode = TopicPanelMode;
}
