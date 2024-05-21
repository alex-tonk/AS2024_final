import {Component, Input, ViewChild} from '@angular/core';
import {UserDto} from "../../../../models/UserDto";
import {UserService} from "../../../../services/user.service";
import {Router} from "@angular/router";
import {ButtonModule} from "primeng/button";
import {CardModule} from "primeng/card";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {MessageService, SharedModule} from "primeng/api";
import {PasswordModule} from "primeng/password";
import {NgIf} from "@angular/common";
import {Cryptic} from "../../../common/Cryptic";
import {firstValueFrom} from "rxjs";
import {PasswordFormComponent} from "../../../forms/user/password-form/password-form.component";
import {InputSwitchModule} from "primeng/inputswitch";
import {UserAccountService} from "../../../../gen/atom2024backend-controllers";

@Component({
  selector: 'app-user-registration',
  standalone: true,
  imports: [
    ButtonModule,
    CardModule,
    FormsModule,
    InputTextModule,
    PaginatorModule,
    SharedModule,
    PasswordModule,
    NgIf,
    PasswordFormComponent,
    InputSwitchModule
  ],
  templateUrl: './user-registration.component.html',
  styleUrl: './user-registration.component.css'
})
export class UserRegistrationComponent {
  loading = false;
  currentPassword: string;
  changePassword = false;

  @Input() isEditMode = false;
  @Input() user = new UserDto();

  @ViewChild(PasswordFormComponent) passwordFormComponent: PasswordFormComponent;

  get passwordEquals(): boolean {
    return this.passwordFormComponent?.password === this.passwordFormComponent?.confirmationPassword;
  }

  constructor(
    private router: Router,
    private messageService: MessageService,
    private userService: UserService,
    private userAccountService: UserAccountService) {
  }

  async createUser() {
    this.loading = true;
    try {
      this.user.password = this.passwordFormComponent.password;
      await this.userService.createUser(this.user);
      this.messageService.add({
        severity: 'success', summary: 'Выполнено',
        detail: 'Пользователь успешно зарегистрирован'
      });
      await this.router.navigate(['/login'])
    } finally {
      this.loading = false;
    }
  }

  async saveUser() {
    this.loading = true;
    try {
      if (this.changePassword) {
        this.user.password = Cryptic.encode(this.passwordFormComponent.password);
      }
      const updatedUser = await firstValueFrom(this.userAccountService
        .updateUser(this.user.id, Cryptic.encode(this.currentPassword), this.user));
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Пользовательские данные обновлены'});
      await this.userService.setUser(updatedUser, true);
    } finally {
      this.loading = false;
    }
  }
}
