import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DialogModule} from "primeng/dialog";
import {UserDto} from "../../../../../models/UserDto";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from "primeng/button";
import {TooltipModule} from "primeng/tooltip";
import {UserAdminService} from "../../../../../gen/atom2024backend-controllers";
import {lastValueFrom} from "rxjs";
import {NgIf} from "@angular/common";
import {PasswordModule} from "primeng/password";
import {MessageService} from "primeng/api";
import {MultiSelectModule} from "primeng/multiselect";
import {RoleDto} from "../../../../../models/RoleDto";
import {Cryptic} from "../../../../common/Cryptic";
import {RadioButtonModule} from "primeng/radiobutton";
import {DropdownModule} from "primeng/dropdown";

@Component({
  selector: 'app-user-registration-form',
  standalone: true,
  imports: [
    DialogModule,
    FormsModule,
    InputTextModule,
    ReactiveFormsModule,
    ButtonModule,
    TooltipModule,
    NgIf,
    PasswordModule,
    MultiSelectModule,
    RadioButtonModule,
    DropdownModule
  ],
  templateUrl: './user-registration-form.component.html',
  styleUrl: './user-registration-form.component.css'
})
export class UserRegistrationFormComponent implements OnInit {
  visible = true;
  loading = false;
  isEditMode = false;
  user = new UserDto();
  roles: RoleDto[] = [];

  selectedRole?: RoleDto;

  @Input() userId?: number;
  @Output() result = new EventEmitter<UserDto | null>();


  constructor(private userAdminService: UserAdminService,
              private messageService: MessageService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      this.roles = await lastValueFrom(this.userAdminService.getRoles());
      if (this.userId) {
        this.user = await lastValueFrom(this.userAdminService.getUser(this.userId, true));
        this.selectedRole = this.user.roles.length > 0 ? this.user.roles[0] : undefined;
        this.isEditMode = true;
      }
    } finally {
      this.loading = false;
    }
  }

  async save() {
    this.loading = true;
    try {
      this.user.roles = !!this.selectedRole ? [this.selectedRole] : [];
      if (this.isEditMode) {
        this.user = await lastValueFrom(this.userAdminService.updateUser(this.user.id, this.user));
      } else {
        this.user.password = Cryptic.encode(this.user.email);
        this.user = await lastValueFrom(this.userAdminService.createUser(this.user));
      }
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Пользователь сохранен'});
      this.result.emit(this.user)
    } finally {
      this.loading = false;
    }
  }

  onHide() {
    this.visible = false;
    this.result.emit(null);
  }

}
