import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  constructor(private httpService: HttpClient) {
  }

  public printDiploma(topicId: number, userId: number): Observable<Blob> {
    return this.httpService.get(`reports/diplomas/${topicId}/${userId}`, {responseType: 'blob'});
  }

  public printDiplomaSpec(topicId: number, userId: number): Observable<Blob> {
    return this.httpService.get(`reports/diplomas/${topicId}/${userId}/specifications`, {responseType: 'blob'});
  }

  public printDiplomaSpecFull(topicId: number, userId: number): Observable<Blob> {
    return this.httpService.get(`reports/diplomas/${topicId}/${userId}/full`, {responseType: 'blob'});
  }
}
