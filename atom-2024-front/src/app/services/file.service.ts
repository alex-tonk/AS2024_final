import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {firstValueFrom} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private restPath = 'files';

  constructor(private http: HttpClient) {
  }

  async upload(standId: number, files: File[]) {
    const formData: FormData = new FormData();
    files.forEach(file => {
      formData.append('uploadFiles[]', file, file.name)
    })
    /*const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');*/

    await firstValueFrom(this.http.post(`${this.restPath}/stands/${standId}`, formData));
  }

  async download() {
    // скачать
  }
}
