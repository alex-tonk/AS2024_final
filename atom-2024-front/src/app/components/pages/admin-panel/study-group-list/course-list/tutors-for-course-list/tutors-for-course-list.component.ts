import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Button} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {
  ColumnFilterWrapperComponent
} from '../../../../../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {DropdownModule} from 'primeng/dropdown';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {MessageService, PrimeTemplate} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {CourseWithTutorsDto, TutorDto, TutorInCourseDto} from '../../../../../../gen/atom2024backend-dto';
import {StudyGroupService} from '../../../../../../gen/atom2024backend-controllers';
import {Column} from '../../../../../common/table/Column';
import {lastValueFrom} from 'rxjs';
import {getField} from '../../../../../../services/field-accessor';

@Component({
  selector: 'app-tutors-for-course-list',
  standalone: true,
  imports: [
    Button,
    CheckboxModule,
    ColumnFilterWrapperComponent,
    DropdownModule,
    FormsModule,
    NgForOf,
    NgIf,
    OverlayPanelModule,
    PrimeTemplate,
    TableModule,
    TooltipModule
  ],
  templateUrl: './tutors-for-course-list.component.html',
  styleUrl: './tutors-for-course-list.component.css'
})
export class TutorsForCourseListComponent implements OnInit {
  @Input() course: CourseWithTutorsDto;
  @Input() allTutors: TutorDto[] = [];
  @Output() result = new EventEmitter<number>();

  tutorsInCourse: TutorInCourseDto[] = [];

  addingTutor: TutorDto | null;

  selected?: TutorInCourseDto;
  loading = false;
  filter = false;

  columns: Column[] = [{
    header: 'ФИО',
    field: 'tutor.user.fullName'
  }];

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  get filteredTutors() {
    if (!this.tutorsInCourse) {
      return this.allTutors;
    }
    return this.allTutors.filter(t => !this.tutorsInCourse.map(tc => tc.tutor?.id).includes(t.id))
  }

  constructor(
    private studyGroupService: StudyGroupService,
    private messageService: MessageService
  ) {
  }

  ngOnInit() {
    this.init();
  }

  async init() {
    this.loading = true;
    try {
      this.tutorsInCourse = await lastValueFrom(this.studyGroupService.getTutorsGET(this.course.studyGroup?.id!, this.course.course?.id!));
    } finally {
      this.loading = false;
    }
  }

  async addTutorToCourse() {
    this.loading = true;
    try {
      if (this.course) {
        await lastValueFrom(this.studyGroupService
          .addTutor(this.course.studyGroup?.id!, this.course.course?.id!, this.addingTutor?.id!));
        this.messageService.add({
          severity: 'success',
          summary: 'Выполнено',
          detail: `${this.addingTutor?.user?.fullName} добавлен для курса ${this.course.course?.name} `
        });
        this.addingTutor = null;
        await this.init();
      }
    } finally {
      this.loading = false;
    }
  }

  async removeTutorFromCourse() {
    this.loading = true;
    try {
      if (this.course && this.selected) {
        await lastValueFrom(this.studyGroupService.removeTutor(this.course.studyGroup?.id!, this.course.course?.id!, this.selected.tutor?.id!));
        this.messageService.add({
          severity: 'success',
          summary: 'Выполнено',
          detail: `${this.selected.tutor?.user?.fullName} удален для курса ${this.course.course?.name} `
        });
        this.selected = undefined;
        await this.init();
      }
    } finally {
      this.loading = false;
    }
  }

  protected readonly getField = getField;
}
