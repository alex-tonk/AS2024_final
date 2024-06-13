import {Component, Input, OnInit} from '@angular/core';
import {CourseService, StudentService} from '../../../../gen/atom2024backend-controllers';
import {CourseDto, StudentDto, StudyGroupDto} from '../../../../gen/atom2024backend-dto';
import {lastValueFrom} from 'rxjs';
import {NgForOf, NgIf} from '@angular/common';
import {OrderListModule} from 'primeng/orderlist';
import {ListboxModule} from 'primeng/listbox';
import {FormsModule} from '@angular/forms';
import {SplitterModule} from 'primeng/splitter';
import {CoursePanelComponent, CoursePanelMode} from '../../../forms/course-panel/course-panel.component';

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
    CoursePanelComponent
  ],
  templateUrl: './study-group-card.component.html',
  styleUrl: './study-group-card.component.css'
})
export class StudyGroupCardComponent implements OnInit {
  @Input() studyGroup: StudyGroupDto;

  courses: CourseDto[] = [];
  students: StudentDto[] = [];

  selectedCourse: CourseDto;
  selectedStudent: StudentDto;

  loading = false;

  constructor(
    private courseService: CourseService,
    private studentService: StudentService,
  ) {
  }

  ngOnInit(): void {
    this.init();
  }

  async init() {
    this.loading = true;
    try {
      this.courses = await lastValueFrom(this.courseService.getCourses());
      this.students = await lastValueFrom(this.studentService.getStudents());
    } finally {
      this.loading = false;
    }
  }

  protected readonly CoursePanelMode = CoursePanelMode;
}
