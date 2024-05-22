import {Component} from '@angular/core';
import {TabViewModule} from "primeng/tabview";
import {UserListComponent} from "./user-list/user-list.component";
import {UserListLazyComponent} from "./user-list-lazy/user-list-lazy.component";
import {RestorePasswordComponent} from "../login/restore-password/restore-password.component";
import {UserRegistrationComponent} from "../login/user-registration/user-registration.component";
import {CodeEditorComponent} from "../../common/code-editor/code-editor.component";

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [
    TabViewModule,
    UserListComponent,
    UserListLazyComponent,
    RestorePasswordComponent,
    UserRegistrationComponent,
    CodeEditorComponent
  ],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css'
})
export class AdminPanelComponent {
  virtualTests: any[] = [];
  constructor() {
  }

  onEditVirtualTest() {
    this.virtualTests.push({id: 1, name: 'Виртуальное испытание резни'})
  }
}
