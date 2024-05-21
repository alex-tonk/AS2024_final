import {Injectable} from '@angular/core';
import {HttpBackend, HttpClient} from "@angular/common/http";
import {lastValueFrom} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private http: HttpClient;
  private appConfig: any;

  constructor(handler: HttpBackend) {
    this.http = new HttpClient(handler);
  }

  async loadConfig() {
    this.appConfig = await lastValueFrom(this.http.get('/assets/config.json'));
  }

  get baseUrl() {
    if (!this.appConfig) {
      throw Error('Config file not loaded!');
    }
    return this.appConfig["baseUrl"];
  }
}
