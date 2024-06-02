import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {StudentDto} from "../../../../../gen/atom2024backend-dto";
import {StudentService, UserAdminService} from "../../../../../gen/atom2024backend-controllers";
import {Footer, MessageService} from "primeng/api";
import {lastValueFrom} from "rxjs";
import {Button} from "primeng/button";
import {DialogModule} from "primeng/dialog";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {DropdownModule} from "primeng/dropdown";
import {UserDto} from "../../../../../models/UserDto";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-student-registration-form',
  standalone: true,
  imports: [
    Button,
    DialogModule,
    Footer,
    FormsModule,
    InputTextModule,
    DropdownModule,
    NgIf
  ],
  templateUrl: './student-registration-form.component.html',
  styleUrl: './student-registration-form.component.css'
})
export class StudentRegistrationFormComponent implements OnInit {
  visible = true;
  loading = false;
  isEditMode = false;
  student = new StudentDto();
  users: UserDto[];

  @Input() studentId?: number;
  @Output() result = new EventEmitter<StudentDto | null>();

  get fullName() {
    if (this.student.user) {
      return `${this.student.user.lastname} ${this.student.user.firstname} ${this.student.user.surname}`;
    } else {
      return '-';
    }
  }
  constructor(private studentService: StudentService,
              private userAdminService: UserAdminService,
              private messageService: MessageService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      if (this.studentId) {
        this.student = await lastValueFrom(this.studentService.getStudent(this.studentId));
        this.isEditMode = true;
      } else {
        this.users = await lastValueFrom(this.userAdminService.getUsers());
      }
    } finally {
      this.loading = false;
    }
  }

  async save() {
    this.loading = true;
    try {
      if (this.isEditMode) {
        this.student = await lastValueFrom(this.studentService.updateStudent(this.student.id!, this.student));
      } else {
        this.student = await lastValueFrom(this.studentService.createStudent(this.student));
      }
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Ученик сохранен'});
      this.result.emit(this.student)
    } finally {
      this.loading = false;
    }
  }

  onHide() {
    this.visible = false;
    this.result.emit(null);
  }
}
