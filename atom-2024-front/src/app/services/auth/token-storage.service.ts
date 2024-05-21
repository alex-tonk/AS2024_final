import {Injectable} from '@angular/core';

export const FAKE_TOKEN = 'fakeToken';
const ACCESS_TOKEN_KEY = 'accessTokenKey';
const TOKEN_EXPIRES_AT = 'tokenExpiresAt';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() {}

  private get tokenExpiresAt(): Date | null {
    const expiresAt = localStorage.getItem(TOKEN_EXPIRES_AT);
    if (expiresAt == null) return null;
    return new Date(expiresAt);
  }

  public saveAccessToken(token: string | null, expiresAt: string | null): void {
    if(token != null && expiresAt != null) {
      localStorage.setItem(ACCESS_TOKEN_KEY, token);
      localStorage.setItem(TOKEN_EXPIRES_AT, expiresAt);
    }
  }

  public getAccessToken(): string | null {
    const tokenExpiresAt = this.tokenExpiresAt;
    if (tokenExpiresAt == null || tokenExpiresAt <= new Date()) {
      this.deleteAccessToken();
      return null;
    }
    return localStorage.getItem(ACCESS_TOKEN_KEY) as string;
  }

  public deleteAccessToken() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(TOKEN_EXPIRES_AT);
  }
}
