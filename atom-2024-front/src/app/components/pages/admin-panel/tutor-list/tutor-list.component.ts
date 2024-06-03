import {Component, OnInit} from '@angular/core';
import {TutorDto} from '../../../../gen/atom2024backend-dto';
import {Column} from '../../../common/table/Column';
import {TutorService} from '../../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {Object} from 'core-js';
import {ExportTable} from '../../../common/table/ExportTable';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {Button} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {
  ColumnFilterWrapperComponent
} from '../../../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {InputTextModule} from 'primeng/inputtext';
import {MenuModule} from 'primeng/menu';
import {TableModule} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {FormsModule} from '@angular/forms';
import {TutorRegistrationFormComponent} from './tutor-registration-form/tutor-registration-form.component';
import {getField} from '../../../../services/field-accessor';

@Component({
  selector: 'app-tutor-list',
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
    TutorRegistrationFormComponent,
    MenuModule,
    AsyncPipe,
    NgIf
  ],
  templateUrl: './tutor-list.component.html',
  styleUrl: './tutor-list.component.css'
})
export class TutorListComponent implements OnInit {
  tutors: TutorDto[] = [];
  selected?: TutorDto;
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

  tutorFormData: { tutorId?: number } | null;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(
    private tutorService: TutorService) {
  }

  async ngOnInit() {
    await this.initTable();
  }

  async initTable() {
    await this.getTutorsFromApi();
  }

  async getTutorsFromApi() {
    this.tutors = [];
    this.selected = undefined;
    this.loading = true;
    try {
      this.tutors = (await lastValueFrom(this.tutorService.getTutors()))
        .map(s => {
          const tutor = Object.assign(new TutorDto(), s);
          // TODO маппинг ФИО
          if (tutor.user) {
            tutor.fullName = `${tutor.user.lastname} ${tutor.user.firstname} ${tutor.user.surname}`
          }
          return tutor;
        });
    } finally {
      this.loading = false;
    }
  }

  createTutor() {
    this.tutorFormData = {};
  }

  editTutor() {
    this.tutorFormData = {tutorId: this.selected?.id};
  }

  async onTutorFormResult(result: TutorDto | null) {
    this.tutorFormData = null;
    if (result) {
      await this.initTable();
    }
  }

  protected readonly ExportTable = ExportTable;
  protected readonly getField = getField;
}
