import {Injectable} from '@angular/core';
import {TokenStorageService} from "./token-storage.service";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {firstValueFrom} from "rxjs";
import {UserDto} from "../../models/UserDto";
import {Router} from "@angular/router";
import {UserService} from "../user.service";
import {Cryptic} from "../../components/common/Cryptic";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private restPath = 'authentication';

  constructor(private http: HttpClient,
              private router: Router,
              private userService: UserService,
              private tokenStorage: TokenStorageService) {
  }

  public async login(email: string, password: string): Promise<UserDto | null> {
    const response: HttpResponse<UserDto> = await firstValueFrom(
      this.http.post<UserDto>(
        `${this.restPath}`,
        {email: email, password: Cryptic.encode(password)},
        {observe: 'response'}
      )
    );

    if(response.body == null) {
      return null;
    }

    this.tokenStorage.saveAccessToken(response.headers.get("Authorization"), response.headers.get("Expires"));
    await this.userService.setUser(response.body);
    return response.body;
  }

  async logout() {
    await firstValueFrom(this.http.delete(`${this.restPath}`));
    this.userService.user = null;
    this.tokenStorage.deleteAccessToken();
    await this.router.navigate(['/login']);
  }
}
