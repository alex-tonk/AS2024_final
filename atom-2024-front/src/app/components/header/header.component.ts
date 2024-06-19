import {Component, HostListener} from '@angular/core';
import {RouterLink} from '@angular/router';
import {TooltipModule} from 'primeng/tooltip';
import {ButtonModule} from 'primeng/button';
import {UserService} from '../../services/user.service';
import {NgForOf, NgIf} from '@angular/common';
import {AuthService} from '../../services/auth/auth.service';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {SidebarModule} from 'primeng/sidebar';
import {BadgeModule} from 'primeng/badge';
import {NotificationService} from '../../gen/atom2024backend-controllers';
import {NotificationDto} from '../../gen/atom2024backend-dto';
import {lastValueFrom} from 'rxjs';
import {NotificationType} from '../../gen/entities-enums';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    TooltipModule,
    ButtonModule,
    NgIf,
    OverlayPanelModule,
    SidebarModule,
    NgForOf,
    BadgeModule
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  isMobileMode = false;
  isAlertPanel = false;
  alerts: NotificationDto[] = [];

  get userRolesString() {
    if (this.userService.user && this.userService.user.rolesAsString) {
      return `Текущая роль: ${this.userService.user.rolesAsString}`;
    } else {
      return 'Нет доступных ролей'
    }
  }

  get logoRouterLink() {
    return this.userService.user ? '' : 'login'
  }

  get userName() {
    const user = this.userService.user;
    return user ? `${user.lastname} ${user.firstname[0].toUpperCase()}. ${user.surname ? user.surname[0].toUpperCase() + '.' : ''}` : 'Пользователь не авторизован';
  }

  get alertsCount() {
    return this.alerts ? this.alerts.length > 9 ? '10+' : `${this.alerts.length}` : '';
  }

  @HostListener('window:resize', ['$event'])
  checkMobile() {
    this.isMobileMode = window.innerWidth < 960;
  }

  constructor(public userService: UserService,
              private authService: AuthService,
              private notificationService: NotificationService) {
    this.checkMobile();
    setInterval(async () => {
      if (this.userService.user == null) return;
      let key = `${userService.user?.id}|lastReadNotification`;
      let lastReadNotification = Math.max(+(localStorage.getItem(key) ?? '0'), 0);
      let newAlerts = await lastValueFrom(this.notificationService.getNewNotifications(lastReadNotification));
      this.alerts = [...newAlerts, ...this.alerts];
      let number = Math.max(...newAlerts.map(a => a.id!), lastReadNotification);
      localStorage.setItem(key, number.toString())
    }, 2000);
  }

  async logout() {
    await this.authService.logout();
  }

  alertMessage(alert: NotificationDto) {
    switch (alert.type) {
      case NotificationType.VALIDATION: {
        return `Вам на проверку поступило задание:\nОбучающийся: ${alert.attempt?.user?.fullName}`
      }
      case NotificationType.DONE: {
        return `Ваше задание проверено`;
      }
      case NotificationType.TIMEOUT: {
        return `Задание было завершено автоматически по истечении времени`;
      }
    }
    return '';
  }
}
