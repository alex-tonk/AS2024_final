import {Component} from '@angular/core';
import {ButtonModule} from "primeng/button";
import {CardModule} from "primeng/card";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {AuthService} from "../../../services/auth/auth.service";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";
import {ToastModule} from "primeng/toast";
import {PasswordModule} from "primeng/password";
import {UserDto} from "../../../models/UserDto";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ButtonModule,
    CardModule,
    FormsModule,
    InputTextModule,
    ToastModule,
    PasswordModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loading = false;

  email: string;
  password: string;

  constructor(
    private messageService: MessageService,
    private authService: AuthService,
    private router: Router) {
  }

  async login() {
    this.loading = true;
    try {
      let user: UserDto | null = await this.authService.login(this.email, this.password);
      if (user == null) {
        this.showLoginErrorMessage();
      } else {
        this.messageService.add({severity: 'info', summary: 'Информация', detail: 'Вы успешно авторизованы'});
        await this.router.navigate(['']);
      }
    } catch (e) {
      this.showLoginErrorMessage();
    } finally {
      this.loading = false;
    }
  }

  async createUser() {
    await this.router.navigate(['users/registration']);
  }

  private showLoginErrorMessage() {
    this.messageService.add({severity: 'error', summary: 'Ошибка', detail: 'Авторизация не удалась'});
  }

  async restorePassword() {
    await this.router.navigate(['users/restore-password']);
  }
}
