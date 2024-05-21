import {Component} from '@angular/core';
import {TabViewModule} from "primeng/tabview";
import {UserListComponent} from "../admin-panel/user-list/user-list.component";
import {UserListLazyComponent} from "../admin-panel/user-list-lazy/user-list-lazy.component";
import {UserRegistrationComponent} from "../login/user-registration/user-registration.component";
import {UserDto} from "../../../models/UserDto";
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-user-panel',
  standalone: true,
    imports: [
        TabViewModule,
        UserListComponent,
        UserListLazyComponent,
        UserRegistrationComponent
    ],
  templateUrl: './user-panel.component.html',
  styleUrl: './user-panel.component.css'
})
export class UserPanelComponent {
  user: UserDto;

  constructor(private userService: UserService) {
    this.user = this.userService.user ? Object.assign(new UserDto(), this.userService.user) : new UserDto();
  }
}
