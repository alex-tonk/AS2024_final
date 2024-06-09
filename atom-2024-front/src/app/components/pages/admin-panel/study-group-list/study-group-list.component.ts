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
import {TableModule, TableRowCollapseEvent, TableRowExpandEvent} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {TutorRegistrationFormComponent} from '../tutor-list/tutor-registration-form/tutor-registration-form.component';
import {CourseDto, StudentDto, StudyGroupDto} from '../../../../gen/atom2024backend-dto';
import {Column} from '../../../common/table/Column';
import {CourseService, StudentService, StudyGroupService} from '../../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {Object} from 'core-js';
import {ExportTable} from '../../../common/table/ExportTable';
import {getField} from '../../../../services/field-accessor';
import {FormsModule} from '@angular/forms';
import {
  CourseRegistrationFormComponent
} from '../course-list/course-registration-form/course-registration-form.component';
import {
  StudyGroupRegistrationFormComponent
} from './study-group-registration-form/study-group-registration-form.component';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {DropdownModule} from 'primeng/dropdown';
import {CourseListComponent} from './course-list/course-list.component';
import {StudentListComponent} from './student-list/student-list.component';
import {SplitterModule} from 'primeng/splitter';

@Component({
  selector: 'app-study-group-list',
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
    TutorRegistrationFormComponent,
    FormsModule,
    CourseRegistrationFormComponent,
    StudyGroupRegistrationFormComponent,
    OverlayPanelModule,
    DropdownModule,
    CourseListComponent,
    StudentListComponent,
    SplitterModule
  ],
  templateUrl: './study-group-list.component.html',
  styleUrl: './study-group-list.component.css'
})
export class StudyGroupListComponent implements OnInit {
  studyGroups: StudyGroupDto[] = [];
  selected?: StudyGroupDto;
  loading = false;
  filter = false;

  allCourses: CourseDto[] = [];
  allStudents: StudentDto[] = [];

  columns: Column[] = [{
    header: 'ID',
    field: 'id',
    type: 'numeric',
    width: 10
  }, {
    header: 'Номер группы',
    field: 'name'
  }, {
    header: 'Кол-во курсов',
    field: 'coursesCount',
    type: 'numeric',
    width: 15
  }, {
    header: 'Кол-во учащихся',
    field: 'studentsCount',
    type: 'numeric',
    width: 15
  }, {
    header: 'Архив',
    field: 'archived',
    type: 'boolean',
    width: 10
  }];

  expandedRows = {};

  studyGroupFormData: { studyGroupId?: number } | null;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(
    private studyGroupService: StudyGroupService,
    private courseService: CourseService,
    private studentService: StudentService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {
  }

  async ngOnInit() {
    await this.getReferencesFromApi();
    await this.initTable();
  }

  async initTable() {
    await this.getStudyGroupsFromApi();
  }

  async getReferencesFromApi() {
    this.loading = true;
    try {
      this.allCourses = await lastValueFrom(this.courseService.getCourses());
      this.allStudents = await lastValueFrom(this.studentService.getStudents());
    } finally {
      this.loading = false;
    }
  }

  async getStudyGroupsFromApi() {
    this.studyGroups = [];
    this.selected = undefined;
    this.loading = true;
    try {
      this.studyGroups = (await lastValueFrom(this.studyGroupService.getStudyGroups()))
        .map(c => {
          const group: StudyGroupDto = Object.assign(new StudyGroupDto(), c);
          group.studentsCount = group.students?.length ?? 0;
          group.coursesCount = group.courses?.length ?? 0;
          return group;
        });
    } finally {
      this.loading = false;
    }
  }

  createStudyGroup() {
    this.studyGroupFormData = {};
  }

  editStudyGroup() {
    this.studyGroupFormData = {studyGroupId: this.selected?.id};
  }


  confirmArchive(event: Event) {
    this.confirmationService.confirm({
      key: 'popup',
      target: event.target as EventTarget,
      icon: 'pi pi-info-circle',
      message: 'Вы действительно хотите архивировать выбранную учебную группу?',
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
      await this.getStudyGroupsFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Учебная группа добавлена в архив'});
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
      await this.getStudyGroupsFromApi();
      this.messageService.add({
        severity: 'success',
        summary: 'Выполнено',
        detail: 'Учебная группа восстановлена из архива'
      });
    } finally {
      this.loading = false;
    }
  }

  async onStudyGroupFormResult(result: StudyGroupDto | null) {
    this.studyGroupFormData = null;
    if (result) {
      await this.initTable();
    }
  }

  async onRowExpand(event: TableRowExpandEvent) {
  }

  onRowCollapse(event: TableRowCollapseEvent) {
  }

  protected readonly ExportTable = ExportTable;
  protected readonly getField = getField;
}
