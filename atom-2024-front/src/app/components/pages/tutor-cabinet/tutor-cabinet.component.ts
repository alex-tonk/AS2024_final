import {Component, OnInit} from '@angular/core';
import {PrimeTemplate} from 'primeng/api';
import {TabViewModule} from 'primeng/tabview';
import {VideoLessonsComponent} from '../student-cabinet/samples/video-lessons/video-lessons.component';
import {CardModule} from 'primeng/card';
import {RouterLink} from '@angular/router';
import {StudyGroupCardComponent} from './study-group-card/study-group-card.component';
import {NgForOf} from '@angular/common';
import {DataViewModule} from 'primeng/dataview';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {OnlineLessonsComponent} from '../student-cabinet/samples/online-lessons/online-lessons.component';
import {
  PresentationLessonsComponent
} from '../student-cabinet/samples/presentation-lessons/presentation-lessons.component';
import {SplitterModule} from 'primeng/splitter';
import {CoursePanelComponent} from '../../forms/course-panel/course-panel.component';
import {AttemptComponent, AttemptListMode} from '../../attempt/attempt.component';

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
    AttemptComponent
  ],
  templateUrl: './tutor-cabinet.component.html',
  styleUrl: './tutor-cabinet.component.css'
})
export class TutorCabinetComponent implements OnInit {
  activeIndex = 0;
  loading = false;

  constructor() {
  }

  async ngOnInit() {
  }

    protected readonly AttemptListMode = AttemptListMode;
}
