import {Component, OnInit} from '@angular/core';
import {PrimeTemplate} from 'primeng/api';
import {TabViewModule} from 'primeng/tabview';
import {VideoLessonsComponent} from '../student-cabinet/samples/video-lessons/video-lessons.component';
import {CardModule} from 'primeng/card';
import {RouterLink} from '@angular/router';
import {StudyGroupCardComponent} from './study-group-card/study-group-card.component';
import {NgForOf} from '@angular/common';
import {setTimeout} from 'core-js';
import {DataViewModule} from 'primeng/dataview';
import {StudyGroupService} from '../../../gen/atom2024backend-controllers';
import {StudyGroupDto} from '../../../gen/atom2024backend-dto';
import {lastValueFrom} from 'rxjs';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {OnlineLessonsComponent} from '../student-cabinet/samples/online-lessons/online-lessons.component';
import {PresentationLessonsComponent} from '../student-cabinet/samples/presentation-lessons/presentation-lessons.component';

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
    FormsModule
  ],
  templateUrl: './tutor-cabinet.component.html',
  styleUrl: './tutor-cabinet.component.css'
})
export class TutorCabinetComponent implements OnInit {
  loading = false;
  activeIndex = 0;

  systemTabsCount = 3;
  // TODO группы для препода!
  availableStudyGroups: StudyGroupDto[] = [];
  openedGroups: { value: StudyGroupDto, title: string }[] = [];

  filterValue: string;

  get filteredGroups() {
    if (this.filterValue) {
      return this.availableStudyGroups.filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
    } else {
      return this.availableStudyGroups;
    }
  }

  constructor(private studyGroupService: StudyGroupService) {
  }

  async ngOnInit() {
    await this.init();
  }

  async init() {
    this.loading = true;
    try {
      this.availableStudyGroups = await lastValueFrom(this.studyGroupService.getStudyGroupsForTutor());
    } finally {
      this.loading = false;
    }
  }

  splitCourses(courseNames: string): string[] {
    return courseNames?.split(',');
  }

  openGroup(group: StudyGroupDto) {
    const idx = this.openedGroups.findIndex(g => g.value.id === group.id);
    if (idx > -1) {
      this.activeIndex = idx + this.systemTabsCount;
    } else {
      this.openedGroups.push({value: group, title: group.name!});
      setTimeout(() => {
        this.activeIndex = this.openedGroups.length + this.systemTabsCount - 1;
      });
    }
  }

  closeGroup(index: number) {
    this.activeIndex = 0;
    this.openedGroups.splice(index - this.systemTabsCount, 1);
  }
}
