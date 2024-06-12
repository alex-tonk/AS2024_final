import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CourseDto, CourseWithTutorsDto, StudyGroupDto} from '../../../../../gen/atom2024backend-dto';
import {ScrollerModule} from 'primeng/scroller';
import {TableModule} from 'primeng/table';
import {Column} from '../../../../common/table/Column';
import {
  ColumnFilterWrapperComponent
} from '../../../../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {CheckboxModule} from 'primeng/checkbox';
import {FormsModule} from '@angular/forms';
import {getField} from '../../../../../services/field-accessor';
import {NgForOf, NgIf} from '@angular/common';
import {Button} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {TooltipModule} from 'primeng/tooltip';
import {DropdownModule} from 'primeng/dropdown';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {lastValueFrom} from 'rxjs';
import {StudyGroupService} from '../../../../../gen/atom2024backend-controllers';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [
    ScrollerModule,
    TableModule,
    ColumnFilterWrapperComponent,
    CheckboxModule,
    FormsModule,
    NgForOf,
    NgIf,
    Button,
    InputTextModule,
    TooltipModule,
    DropdownModule,
    OverlayPanelModule
  ],
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.css'
})
export class CourseListComponent implements OnInit {
  @Input() studyGroup: StudyGroupDto;
  @Input() allCourses: CourseDto[] = [];

  coursesInGroup: CourseWithTutorsDto[] = [];

  @Output() result = new EventEmitter<number>();

  get filteredCourses() {
    if (!this.coursesInGroup) {
      return this.allCourses;
    }
    return this.allCourses.filter(c => !this.coursesInGroup.map(cg => cg.course?.id).includes(c.id))
  }

  constructor(
    private studyGroupService: StudyGroupService,
    private messageService: MessageService
  ) {
  }

  addingCourse: CourseDto | null;

  selected?: CourseWithTutorsDto;
  loading = false;
  filter = false;

  columns: Column[] = [{
    header: 'Название курса',
    field: 'course.name'
  }];

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  ngOnInit() {
    this.init();
  }

  async init() {
    this.loading = true;
    try {
      this.coursesInGroup = await lastValueFrom(this.studyGroupService.getCourses(this.studyGroup.id!));
    } finally {
      this.loading = false;
    }
  }

  async addCourseToGroup() {
    this.loading = true;
    try {
      if (this.studyGroup?.id && this.addingCourse?.id) {
        const result = await lastValueFrom(this.studyGroupService.addCourse(this.studyGroup.id, this.addingCourse.id));
        this.messageService.add({
          severity: 'success',
          summary: 'Выполнено',
          detail: `Курс ${this.addingCourse.name} добавлен для группы ${this.studyGroup.name} `
        });
        this.addingCourse = null;
        await this.init();
        this.result.emit(this.studyGroup.id);
      }
    } finally {
      this.loading = false;
    }
  }

  async removeCourseFromGroup() {
    this.loading = true;
    try {
      if (this.studyGroup?.id && this.selected?.course?.id) {
        await lastValueFrom(this.studyGroupService.removeCourse(this.studyGroup.id, this.selected.course.id));
        this.messageService.add({
          severity: 'success',
          summary: 'Выполнено',
          detail: `Курс ${this.selected.course.name} удален для группы ${this.studyGroup.name} `
        });
        this.selected = undefined;
        await this.init();
        this.result.emit(this.studyGroup.id);
      }
    } finally {
      this.loading = false;
    }
  }

  protected readonly getField = getField;
}
