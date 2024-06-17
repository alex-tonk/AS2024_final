import {Component, Input, OnInit} from '@angular/core';
import {StudyGroupService} from '../../../../gen/atom2024backend-controllers';
import {
  CourseDto,
  CourseWithTutorsDto,
  StudentDto,
  StudentInGroupDto,
  StudyGroupDto
} from '../../../../gen/atom2024backend-dto';
import {lastValueFrom} from 'rxjs';
import {NgForOf, NgIf} from '@angular/common';
import {OrderListModule} from 'primeng/orderlist';
import {ListboxModule} from 'primeng/listbox';
import {FormsModule} from '@angular/forms';
import {SplitterModule} from 'primeng/splitter';
import {TopicPanelComponent, TopicPanelMode} from '../../../forms/lesson-panel/topic-panel.component';

@Component({
  selector: 'app-study-group-card',
  standalone: true,
  imports: [
    NgForOf,
    OrderListModule,
    ListboxModule,
    FormsModule,
    SplitterModule,
    NgIf,
    TopicPanelComponent
  ],
  templateUrl: './study-group-card.component.html',
  styleUrl: './study-group-card.component.css'
})
export class StudyGroupCardComponent implements OnInit {
  @Input() studyGroup: StudyGroupDto;

  courses: CourseWithTutorsDto[] = [];
  students: StudentInGroupDto[] = [];

  selectedCourse: CourseDto;
  selectedStudent: StudentDto;

  loading = false;

  constructor(private studyGroupService: StudyGroupService) {
  }

  async ngOnInit() {
    await this.init();
  }

  async init() {
    this.loading = true;
    try {
      // TODO Курсы группы для препода, студенты группы
      this.courses = await lastValueFrom(this.studyGroupService.getCoursesForTutor(this.studyGroup.id!));
      this.students = await lastValueFrom(this.studyGroupService.getStudents(this.studyGroup.id!));
    } finally {
      this.loading = false;
    }
  }

  protected readonly CoursePanelMode = TopicPanelMode;
}
