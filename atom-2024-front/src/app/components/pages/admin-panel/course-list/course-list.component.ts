import {Component, OnInit} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {Button} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {
  ColumnFilterWrapperComponent
} from '../../../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {InputTextModule} from 'primeng/inputtext';
import {MenuModule} from 'primeng/menu';
import {ConfirmationService, MessageService, PrimeTemplate} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {UserRegistrationFormComponent} from '../user-list/user-registration-form/user-registration-form.component';
import {Column} from '../../../common/table/Column';
import {lastValueFrom} from 'rxjs';
import {ExportTable} from '../../../common/table/ExportTable';
import {TopicDto} from '../../../../gen/atom2024backend-dto';
import {Object} from 'core-js';
import {FormsModule} from '@angular/forms';
import {CourseRegistrationFormComponent} from './course-registration-form/course-registration-form.component';
import {getField} from '../../../../services/field-accessor';
import {SplitterModule} from 'primeng/splitter';
import {CoursePanelComponent, CoursePanelMode} from '../../../forms/course-panel/course-panel.component';
import {TopicService} from '../../../../gen/atom2024backend-controllers';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [
    AsyncPipe,
    Button,
    CheckboxModule,
    ColumnFilterWrapperComponent,
    InputTextModule,
    MenuModule,
    NgForOf,
    NgIf,
    PrimeTemplate,
    TableModule,
    TooltipModule,
    UserRegistrationFormComponent,
    FormsModule,
    CourseRegistrationFormComponent,
    SplitterModule,
    CoursePanelComponent
  ],
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.css'
})
export class CourseListComponent implements OnInit {
  courses: TopicDto[] = [];
  selected?: TopicDto;
  loading = false;
  filter = false;

  columns: Column[] = [{
    header: 'ID',
    field: 'id',
    type: 'numeric',
    width: 10
  }, {
    header: 'Название темы',
    field: 'name'
  }, {
    header: 'Архив',
    field: 'archived',
    type: 'boolean',
    width: 10
  }]

  courseFormData: { courseId?: number } | null;
  isCourseConstructorMode = false;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(
    private topicService: TopicService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {
  }

  async ngOnInit() {
    await this.initTable();
  }

  async initTable() {
    await this.getCoursesFromApi();
  }

  async getCoursesFromApi() {
    this.courses = [];
    this.selected = undefined;
    this.loading = true;
    try {
      this.courses = (await lastValueFrom(this.topicService.getTopics()));
    } finally {
      this.loading = false;
    }
  }

  createCourse() {
    this.courseFormData = {};
  }

  editCourse() {
    this.courseFormData = {courseId: this.selected?.id};
  }


  confirmArchive(event: Event) {
    this.confirmationService.confirm({
      key: 'popup',
      target: event.target as EventTarget,
      icon: 'pi pi-info-circle',
      message: 'Вы действительно хотите архивировать выбранную тему?',
      accept: () => this.archive()
    });
  }

  async archive() {
    if (!this.selected) {
      return;
    }
    try {
      this.loading = true;
      await this.getCoursesFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Тема добавлена в архив'});
    } finally {
      this.loading = false;
    }
  }

  async unarchive() {
    if (!this.selected) {
      return;
    }
    try {
      this.loading = true;
      await this.getCoursesFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Тема восстановлена из архива'});
    } finally {
      this.loading = false;
    }
  }

  async onCourseFormResult(result: TopicDto | null) {
    this.courseFormData = null;
    if (result) {
      await this.initTable();
    }
  }

  protected readonly ExportTable = ExportTable;
  protected readonly getField = getField;
  protected readonly CoursePanelMode = CoursePanelMode;
}
