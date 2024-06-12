import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Button} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {
    ColumnFilterWrapperComponent
} from '../../../../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {DropdownModule} from 'primeng/dropdown';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {PaginatorModule} from 'primeng/paginator';
import {MessageService, PrimeTemplate} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {TooltipModule} from 'primeng/tooltip';
import {StudentDto, StudentInGroupDto, StudyGroupDto} from '../../../../../gen/atom2024backend-dto';
import {StudyGroupService} from '../../../../../gen/atom2024backend-controllers';
import {Column} from '../../../../common/table/Column';
import {lastValueFrom} from 'rxjs';
import {getField} from '../../../../../services/field-accessor';

@Component({
    selector: 'app-student-list',
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
        PaginatorModule,
        PrimeTemplate,
        TableModule,
        TooltipModule
    ],
    templateUrl: './student-list.component.html',
    styleUrl: './student-list.component.css'
})
export class StudentListComponent implements OnInit {
    @Input() studyGroup: StudyGroupDto;
    @Input() allStudents: StudentDto[] = [];

    studentsInGroup: StudentInGroupDto[] = [];

    @Output() result = new EventEmitter<number>();

    get filteredStudents() {
        if (!this.studentsInGroup) {
            return this.allStudents;
        }
        return this.allStudents.filter(s => !this.studentsInGroup.map(sg => sg.student?.id).includes(s.id))
    }

    constructor(
        private studyGroupService: StudyGroupService,
        private messageService: MessageService
    ) {
    }

    addingStudent: StudentDto | null;

    selected?: StudentInGroupDto;
    loading = false;
    filter = false;

    columns: Column[] = [{
        header: 'ФИО',
        field: 'student.user.fullName'
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
            this.studentsInGroup = await lastValueFrom(this.studyGroupService.getStudents(this.studyGroup.id!));
        } finally {
            this.loading = false;
        }
    }

    async addStudentToGroup() {
        this.loading = true;
        try {
            if (this.studyGroup?.id && this.addingStudent?.id) {
                const result = await lastValueFrom(this.studyGroupService.addStudent(this.studyGroup.id, this.addingStudent.id));
                this.messageService.add({
                    severity: 'success',
                    summary: 'Выполнено',
                    detail: `${this.addingStudent.user?.fullName} добавлен в группу ${this.studyGroup.name} `
                });
                this.addingStudent = null;
                await this.init();
                this.result.emit(this.studyGroup.id);
            }
        } finally {
            this.loading = false;
        }
    }

    async removeStudentFromGroup() {
        this.loading = true;
        try {
            if (this.studyGroup?.id && this.selected?.student?.id) {
                await lastValueFrom(this.studyGroupService.removeStudent(this.studyGroup.id, this.selected.student.id));
                this.messageService.add({
                    severity: 'success',
                    summary: 'Выполнено',
                    detail: `${this.selected.student.user?.fullName} удален из группы ${this.studyGroup.name} `
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
