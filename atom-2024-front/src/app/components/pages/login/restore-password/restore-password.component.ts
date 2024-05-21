import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {MessageService, SharedModule} from "primeng/api";
import {ButtonModule} from "primeng/button";
import {CardModule} from "primeng/card";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {NgIf} from "@angular/common";
import {PaginatorModule} from "primeng/paginator";
import {PasswordModule} from "primeng/password";
import {UserService} from "../../../../services/user.service";
import {PasswordFormComponent} from "../../../forms/user/password-form/password-form.component";
import {Subscription, timer} from "rxjs";
import {MenuModule} from "primeng/menu";

@Component({
  selector: 'app-restore-password',
  standalone: true,
  imports: [
    ButtonModule,
    CardModule,
    FormsModule,
    InputTextModule,
    NgIf,
    PaginatorModule,
    PasswordModule,
    SharedModule,
    PasswordFormComponent,
    MenuModule
  ],
  templateUrl: './restore-password.component.html',
  styleUrl: './restore-password.component.css'
})
export class RestorePasswordComponent implements OnInit {
  loading = false;
  email: string;

  verificationCode?: number;
  isCodeRequested: boolean = false;
  countdown: string = '';
  countdownSubscription: Subscription;

  constructor(
    private router: Router,
    private userService: UserService,
    private messageService: MessageService) {
  }

  async ngOnInit() {
  }

  async requestCode() {
    this.loading = true;
    try {

      await this.userService.sendRestorePasswordCode(this.email);
      this.isCodeRequested = true;

      const countdownMinutes = 15;
      const countdownSeconds = countdownMinutes * 60;

      this.countdownSubscription?.unsubscribe();
      this.countdownSubscription = timer(0, 1000)
        .subscribe((val) => {
          const minutes = Math.floor((countdownSeconds - val) / 60);
          const seconds = (countdownSeconds - val) % 60;
          this.countdown = `${minutes}:${seconds < 10 ? '0' + seconds : seconds}`;

          if (val >= countdownSeconds) {
            this.isCodeRequested = false;
            this.countdown = '';
            this.countdownSubscription.unsubscribe();
          }
        });
      this.messageService.add({
        severity: 'success', summary: 'Выполнено',
        detail: `Проверочный код отправлен на ${this.email}`
      });
    } finally {
      this.loading = false;
    }
  }

  async savePassword(password: string) {
    if (!this.verificationCode) {
      this.messageService.add({severity: 'warn', summary: 'Внимание', detail: 'Введите проверочный код'});
      return;
    }

    this.loading = true;
    try {
      await this.userService.restorePassword(this.email, password, this.verificationCode);
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Новый пароль сохранен'});
      await this.router.navigate(['/login'])
    } finally {
      this.loading = false;
    }
  }
}
