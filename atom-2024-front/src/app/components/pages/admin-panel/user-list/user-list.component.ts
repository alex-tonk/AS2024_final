import {Component, OnInit} from '@angular/core';
import {TableModule} from "primeng/table";
import {UserDto} from "../../../../models/UserDto";
import {ToolbarModule} from "primeng/toolbar";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {Column} from "../../../common/table/Column";
import {AsyncPipe, DatePipe, NgForOf, NgIf} from "@angular/common";
import {ConfirmationService, MessageService} from "primeng/api";
import {TooltipModule} from "primeng/tooltip";
import {CalendarModule} from "primeng/calendar";
import {InputNumberModule} from "primeng/inputnumber";
import {
  ColumnFilterWrapperComponent
} from "../../../common/table/column-filter-wrapper/column-filter-wrapper.component";
import {CheckboxModule} from "primeng/checkbox";
import {FormsModule} from "@angular/forms";
import {firstValueFrom, lastValueFrom} from "rxjs";
import {UserAdminService} from "../../../../gen/atom2024backend-controllers";
import {MultiSelectModule} from "primeng/multiselect";
import {
  UserRegistrationFormComponent
} from "./user-registration-form/user-registration-form.component";
import {UserService} from "../../../../services/user.service";
import {MenuModule} from "primeng/menu";
import {ExportTable} from "../../../common/table/ExportTable";
import {AutoFocusModule} from "primeng/autofocus";

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    TableModule,
    ToolbarModule,
    ButtonModule,
    InputTextModule,
    NgForOf,
    TooltipModule,
    NgIf,
    DatePipe,
    CalendarModule,
    InputNumberModule,
    ColumnFilterWrapperComponent,
    CheckboxModule,
    FormsModule,
    MultiSelectModule,
    UserRegistrationFormComponent,
    MenuModule,
    AutoFocusModule,
    AsyncPipe
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {
  users: UserDto[] = [];
  selectedUser?: UserDto;
  loading = false;
  filter = false;

  columns: Column[] = [{
    header: 'ID',
    field: 'id',
    type: 'numeric',
    width: 10
  }, {
    header: 'ФИО',
    field: 'fullName',
    width: 25
  }, {
    header: 'Почта',
    field: 'email'
  }, {
    header: 'Дата регистрации',
    field: 'registrationDate',
    fieldGetter: 'formattedRegistrationDate',
    type: 'date'
  }, {
    header: 'Роль',
    field: 'rolesAsString'
  }, {
    header: 'Архив',
    field: 'archived',
    type: 'boolean'
  }]

  userFormData: { userId?: number } | null;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(
    private userService: UserService,
    private userAdminService: UserAdminService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {
  }

  async ngOnInit() {
    await this.initTable();
  }

  async initTable() {
    await this.getUsersFromApi();
  }

  async getUsersFromApi() {
    this.users = [];
    this.selectedUser = undefined;
    this.loading = true;
    try {
      this.users = (await lastValueFrom(this.userAdminService.getUsers()))
        .map(u => new UserDto(u));
    } finally {
      this.loading = false;
    }
  }

  createUser() {
    this.userFormData = {};
  }

  editeUser() {
    this.userFormData = {userId: this.selectedUser?.id};
  }

  confirmResetPassword(event: Event) {
    this.confirmationService.confirm({
      key: 'popup',
      target: event.target as EventTarget,
      icon: 'pi pi-info-circle',
      message: 'Вы действительно хотите сбросить пароль выбранного пользователя?',
      accept: () => this.resetPassword()
    });
  }

  async resetPassword() {
    if (!this.selectedUser) {
      return;
    }
    try {
      this.loading = true;
      await firstValueFrom(this.userAdminService.resetPassword(this.selectedUser.id));
      await this.getUsersFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Пароль пользователя сброшен'});
    } finally {
      this.loading = false;
    }
  }

  confirmArchive(event: Event) {
    this.confirmationService.confirm({
      key: 'popup',
      target: event.target as EventTarget,
      icon: 'pi pi-info-circle',
      message: 'Вы действительно хотите архивировать выбранного пользователя?',
      accept: () => this.archive()
    });
  }

  async archive() {
    if (!this.selectedUser) {
      return;
    }
    try {
      this.loading = true;
      await firstValueFrom(this.userAdminService.archiveUser(this.selectedUser.id));
      await this.getUsersFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Пользователь в архиве'});
    } finally {
      this.loading = false;
    }
  }

  async unarchive() {
    if (!this.selectedUser) {
      return;
    }
    try {
      this.loading = true;
      await firstValueFrom(this.userAdminService.unarchiveUser(this.selectedUser.id));
      await this.getUsersFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Пользователь восстановлен'});
    } finally {
      this.loading = false;
    }
  }

  async onUserFormResult(result: UserDto | null) {
    this.userFormData = null;
    if (result) {
      await this.initTable();
    }
  }

  protected readonly ExportTable = ExportTable;
}
