import {CanActivateFn} from '@angular/router';
import {inject} from "@angular/core";
import {UserService} from "../user.service";

export const unauthorizedGuard: CanActivateFn = (route, state) => {
  const user = inject(UserService).user;
  return !user
};
