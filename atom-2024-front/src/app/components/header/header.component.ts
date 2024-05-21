import {Component, HostListener} from '@angular/core';
import {RouterLink} from "@angular/router";
import {TooltipModule} from "primeng/tooltip";
import {ButtonModule} from "primeng/button";
import {UserService} from "../../services/user.service";
import {NgIf} from "@angular/common";
import {AuthService} from "../../services/auth/auth.service";
import {OverlayPanelModule} from "primeng/overlaypanel";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    TooltipModule,
    ButtonModule,
    NgIf,
    OverlayPanelModule
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  isMobileMode = false;
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

  @HostListener('window:resize', ['$event'])
  checkMobile() {
    this.isMobileMode = window.innerWidth < 960;
  }

  constructor(public userService: UserService,
              private authService: AuthService) {
    this.checkMobile();
  }

  async logout() {
    await this.authService.logout();
  }
}
