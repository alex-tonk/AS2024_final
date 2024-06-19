import {Component} from '@angular/core';
import {UserDto} from '../../../../models/UserDto';
import {Column} from '../../../common/table/Column';
import {UserAdminService} from '../../../../gen/atom2024backend-controllers';
import {ConfirmationService, MessageService, SharedModule} from 'primeng/api';
import {firstValueFrom, lastValueFrom} from 'rxjs';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {PageResponse} from '../../../../gen/query-lazy';
import {ButtonModule} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {
  ColumnFilterWrapperComponent
} from '../../../common/table/column-filter-wrapper/column-filter-wrapper.component';
import {InputTextModule} from 'primeng/inputtext';
import {MultiSelectModule} from 'primeng/multiselect';
import {NgForOf, NgIf} from '@angular/common';
import {TooltipModule} from 'primeng/tooltip';
import {FormsModule} from '@angular/forms';
import {getField} from '../../../../services/field-accessor';

@Component({
  selector: 'app-user-list-lazy',
  standalone: true,
  imports: [
    ButtonModule,
    CheckboxModule,
    ColumnFilterWrapperComponent,
    InputTextModule,
    MultiSelectModule,
    NgForOf,
    NgIf,
    SharedModule,
    TableModule,
    TooltipModule,
    FormsModule
  ],
  templateUrl: './user-list-lazy.component.html',
  styleUrl: './user-list-lazy.component.css'
})
export class UserListLazyComponent {
  users: UserDto[] = [];
  total: number = 0;
  selectedUser: UserDto | null;
  loading = false;
  filter = false;

  colDefs: Column[] = [{
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
    header: 'Роли',
    field: 'rolesAsString'
  }, {
    header: 'Архив',
    field: 'archived',
    type: 'boolean'
  }]

  selectedColumns = this.colDefs;

  get columnFields(): string[] {
    const arr = this.colDefs
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.colDefs.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(
    private userAdminService: UserAdminService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {
  }

  async ngOnInit() {
    await this.initTable();
  }

  async initTable() {
    this.loading = true;
    this.selectedUser = null;
    try {
      // await this.getUsersFromApi();
    } finally {
      this.loading = false;
    }
  }

  async getUsersFromApi() {
    this.loading = true;
    try {
      this.users = (await lastValueFrom(this.userAdminService.getUsers(true)))
        .map(u => Object.assign(new UserDto(), u));
    } finally {
      this.loading = false;
    }
  }


  createUser() {
    alert('CREATE WIP')
  }

  editeUser() {
    alert('EDIT WIP')
  }

  confirmDelete(event: Event) {
    this.confirmationService.confirm({
      key: 'popup',
      target: event.target as EventTarget,
      icon: 'pi pi-info-circle',
      message: 'Вы действительно хотите удалить выбранного пользователя?',
      accept: () => this.deleteUser()
    });
  }

  async deleteUser() {
    if (!this.selectedUser) {
      return;
    }
    try {
      this.loading = true;
      await firstValueFrom(this.userAdminService.archiveUser(this.selectedUser.id));
      await this.getUsersFromApi();
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Пользователь удален'});
    } finally {
      this.loading = false;
    }
  }

  async onLazyLoad(event: TableLazyLoadEvent) {
    this.loading = true;
    try {
      if (event == null || event.first == null || event.rows == null || event.forceUpdate == null) {
        return;
      }
      const result: PageResponse<UserDto> = await firstValueFrom(this.userAdminService.searchUsers(event));
      const loadedUsers = result.items!.map(u => {
        const user = new UserDto();
        Object.assign(user, u);
        return user;
      });
      // этому хуесосу надо, чтобы массив был длины как total (первоначально заполням всё undefined)
      if (event.rows == 0 || result.total != this.users.length) {
        this.users = Array.from({length: result.total!});
      }
      this.total = result.total!;
      // заполняем пачку данных
      Array.prototype.splice.apply(this.users, [event.first, event.rows, ...loadedUsers]);
      event.forceUpdate();
    } finally {
      this.loading = false;
    }
  }

  protected readonly getField = getField;
}
