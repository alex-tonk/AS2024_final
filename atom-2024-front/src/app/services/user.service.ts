import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {UserDto} from "../models/UserDto";
import {firstValueFrom} from "rxjs";
import {TokenStorageService} from "./auth/token-storage.service";
import {Router} from "@angular/router";
import {Cryptic} from "../components/common/Cryptic";

@Injectable({
  providedIn: 'root'
})
export class
UserService {

  public user: UserDto | null;
  private restPath = 'authentication';

  constructor(private http: HttpClient,
              private router: Router,
              private tokenStorage: TokenStorageService) {
  }

  async init() {
    await this.setUser(null);
    if (this.user) {
      console.log(`Вы всё ещё авторизованы как ${this.user.email}`);
    }
  }

  async setUser(user: UserDto | null, skipNavigate?: boolean) {
    const token = this.tokenStorage.getAccessToken();
    if (user != null && token != null) {
      this.user = user;
    } else {
      if (token != null) {
        this.user = await this.getUserByToken();
      }
    }

    if (skipNavigate) {
      return;
    }

    if (!this.user) {
      await this.router.navigate(['/login']);
    }
  }

  async getUserByToken(): Promise<UserDto | null> {
    try {
      return await firstValueFrom(this.http.get<UserDto>(`${this.restPath}`));
    } catch (e) {
      console.log('Не удалось найти пользователя по токену, пусть пройдет авторизацию');
      return null;
    }
  }

  async createUser(user: UserDto): Promise<UserDto> {
    user.password = Cryptic.encode(user.password);
    return await firstValueFrom(this.http.post<UserDto>(`${this.restPath}/registration`, user));
  };

  async sendRestorePasswordCode(email: string): Promise<void> {
    await firstValueFrom(this.http.get(`${this.restPath}/restore-password-code`, {
      params: new HttpParams()
        .append("email", email)
    }));
  };

  async restorePassword(email: string, newPassword: string, code: number): Promise<void> {
    await firstValueFrom(this.http.patch<UserDto>(`${this.restPath}/restore-password`, {}, {
      params: new HttpParams()
        .append('email', email)
        .append('newPassword', Cryptic.encode(newPassword))
        .append('code', code)
    }));
  };

  public hasRole(roleName: string): boolean {
    return this.user ? !!this.user.roles?.find(r => r.name === roleName) : false;
  }
}
