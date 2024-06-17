import {Routes} from '@angular/router';
import {LoginComponent} from "./components/pages/login/login.component";
import {MainComponent} from "./components/pages/main/main.component";
import {UserRegistrationComponent} from "./components/pages/login/user-registration/user-registration.component";
import {AdminPanelComponent} from "./components/pages/admin-panel/admin-panel.component";
import {RestorePasswordComponent} from "./components/pages/login/restore-password/restore-password.component";
import {UserPanelComponent} from "./components/pages/user-panel/user-panel.component";
import {adminGuard} from "./services/guards/admin.guard";
import {unauthorizedGuard} from "./services/guards/unauthorized.guard";
import {ChatComponent} from "./components/chat/chat/chat.component";
import {StudentCabinetComponent} from "./components/pages/student-cabinet/student-cabinet.component";
import {TutorCabinetComponent} from './components/pages/tutor-cabinet/tutor-cabinet.component';

export const routes: Routes = [
  {path: '', component: MainComponent},
  {path: 'login', component: LoginComponent, canActivate: [unauthorizedGuard]},
  {
    path: 'users', children: [
      {path: 'registration', component: UserRegistrationComponent},
      {path: 'restore-password', component: RestorePasswordComponent}
    ]
  },
  {path: 'admin-panel', component: AdminPanelComponent, canActivate: [adminGuard]},
  {path: 'user-panel', component: UserPanelComponent},
  {path: 'student-cabinet', component: StudentCabinetComponent},
  {path: 'tutor-cabinet', component: TutorCabinetComponent},
  {path: 'chat', component: ChatComponent},
  {path: '**', redirectTo: ''}
];
