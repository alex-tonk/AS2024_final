import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TutorDto} from "../../../../../gen/atom2024backend-dto";
import {TutorService, UserAdminService} from "../../../../../gen/atom2024backend-controllers";
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
  selector: 'app-tutor-registration-form',
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
  templateUrl: './tutor-registration-form.component.html',
  styleUrl: './tutor-registration-form.component.css'
})
export class TutorRegistrationFormComponent implements OnInit {
  visible = true;
  loading = false;
  isEditMode = false;
  tutor = new TutorDto();
  users: UserDto[];

  @Input() tutorId?: number;
  @Output() result = new EventEmitter<TutorDto | null>();

  get fullName() {
    if (this.tutor.user) {
      return `${this.tutor.user.lastname} ${this.tutor.user.firstname} ${this.tutor.user.surname}`;
    } else {
      return '-';
    }
  }

  constructor(private tutorService: TutorService,
              private userAdminService: UserAdminService,
              private messageService: MessageService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      if (this.tutorId) {
        this.tutor = await lastValueFrom(this.tutorService.getTutor(this.tutorId));
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
        this.tutor = await lastValueFrom(this.tutorService.updateTutor(this.tutor.id!, this.tutor));
      } else {
        this.tutor = await lastValueFrom(this.tutorService.createTutor(this.tutor));
      }
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Преподаватель сохранен'});
      this.result.emit(this.tutor)
    } finally {
      this.loading = false;
    }
  }

  onHide() {
    this.visible = false;
    this.result.emit(null);
  }
}
