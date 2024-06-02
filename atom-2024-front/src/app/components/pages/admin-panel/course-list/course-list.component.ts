import {Component, OnInit} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {Button} from "primeng/button";
import {CheckboxModule} from "primeng/checkbox";
import {
  ColumnFilterWrapperComponent
} from "../../../common/table/column-filter-wrapper/column-filter-wrapper.component";
import {InputTextModule} from "primeng/inputtext";
import {MenuModule} from "primeng/menu";
import {ConfirmationService, MessageService, PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {TooltipModule} from "primeng/tooltip";
import {
  UserRegistrationFormComponent
} from "../user-list/user-registration-form/user-registration-form.component";
import {Column} from "../../../common/table/Column";
import {CourseService} from "../../../../gen/atom2024backend-controllers";
import {lastValueFrom} from "rxjs";
import {ExportTable} from "../../../common/table/ExportTable";
import {CourseDto} from "../../../../gen/atom2024backend-dto";
import {Object} from "core-js";
import {FormsModule} from "@angular/forms";
import {
  CourseRegistrationFormComponent
} from "./course-registration-form/course-registration-form.component";

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
    CourseRegistrationFormComponent
  ],
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.css'
})
export class CourseListComponent implements OnInit {
  courses: CourseDto[] = [];
  selected?: CourseDto;
  loading = false;
  filter = false;

  columns: Column[] = [{
    header: 'ID',
    field: 'id',
    type: 'numeric',
    width: 10
  }, {
    header: 'Название курса',
    field: 'name'
  }, {
    header: 'Архив',
    field: 'archived',
    type: 'boolean',
    width: 10
  }]

  courseFormData: { courseId?: number } | null;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(
    private courseService: CourseService,
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
      this.courses = (await lastValueFrom(this.courseService.getCourses()))
        .map(c => Object.assign(new CourseDto(), c));
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
      message: 'Вы действительно хотите архивировать выбранный курс?',
      accept: () => this.archive()
    });
  }

  async archive() {
    if (!this.selected) {
      return;
    }
    try {
      this.loading = true;
      // TODO archive
      await this.getCoursesFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Курс добавлен в архив'});
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
      // TODO unarchive
      await this.getCoursesFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Курс восстановлен из архива'});
    } finally {
      this.loading = false;
    }
  }

  async onCourseFormResult(result: CourseDto | null) {
    this.courseFormData = null;
    if (result) {
      await this.initTable();
    }
  }

  protected readonly ExportTable = ExportTable;
}
