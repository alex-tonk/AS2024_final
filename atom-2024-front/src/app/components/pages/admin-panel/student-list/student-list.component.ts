import {Component, OnInit} from '@angular/core';
import {StudentDto} from "../../../../gen/atom2024backend-dto";
import {Column} from "../../../common/table/Column";
import {StudentService} from "../../../../gen/atom2024backend-controllers";
import {lastValueFrom} from "rxjs";
import {Object} from "core-js";
import {ExportTable} from "../../../common/table/ExportTable";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {Button} from "primeng/button";
import {CheckboxModule} from "primeng/checkbox";
import {
  ColumnFilterWrapperComponent
} from "../../../common/table/column-filter-wrapper/column-filter-wrapper.component";
import {InputTextModule} from "primeng/inputtext";
import {MenuModule} from "primeng/menu";
import {TableModule} from "primeng/table";
import {TooltipModule} from "primeng/tooltip";
import {FormsModule} from "@angular/forms";
import {StudentRegistrationFormComponent} from "./student-registration-form/student-registration-form.component";

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [
    TableModule,
    Button,
    TooltipModule,
    InputTextModule,
    ColumnFilterWrapperComponent,
    NgForOf,
    CheckboxModule,
    FormsModule,
    StudentRegistrationFormComponent,
    MenuModule,
    AsyncPipe,
    NgIf
  ],
  templateUrl: './student-list.component.html',
  styleUrl: './student-list.component.css'
})
export class StudentListComponent implements OnInit {
  students: StudentDto[] = [];
  selected?: StudentDto;
  loading = false;
  filter = false;

  columns: Column[] = [{
    header: 'ID',
    field: 'id',
    type: 'numeric',
    width: 10
  }, {
    header: 'ФИО',
    field: 'fullName'
  }, {
    header: 'Архив',
    field: 'archived',
    type: 'boolean',
    width: 10
  }]

  studentFormData: { studentId?: number } | null;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(
    private studentService: StudentService) {
  }

  async ngOnInit() {
    await this.initTable();
  }

  async initTable() {
    await this.getStudentsFromApi();
  }

  async getStudentsFromApi() {
    this.students = [];
    this.selected = undefined;
    this.loading = true;
    try {
      this.students = (await lastValueFrom(this.studentService.getStudents()))
        .map(s => {
          const student = Object.assign(new StudentDto(), s);
          // TODO маппинг ФИО
          if (student.user) {
            student.fullName = `${student.user.lastname} ${student.user.firstname} ${student.user.surname}`
          }
          return student;
        });
    } finally {
      this.loading = false;
      console.log(this.students);
    }
  }

  createCourse() {
    this.studentFormData = {};
  }

  editCourse() {
    this.studentFormData = {studentId: this.selected?.id};
  }

  async onStudentFormResult(result: StudentDto | null) {
    this.studentFormData = null;
    if (result) {
      await this.initTable();
    }
  }

  protected readonly ExportTable = ExportTable;
}
