import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AttachmentDto} from '../gen/dto-chat';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  httpService: HttpClient;

  public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

  public uploadFile(file: File): Observable<AttachmentDto> {
    let formData = new FormData();
    formData.append('file', file);
    return this.httpService.post<AttachmentDto>('chats/attachments', formData, {responseType: 'json'});
  }

  public deleteFile(fileId: number): Observable<void> {
    return this.httpService.delete<void>('chats/attachments/' + fileId);
  }

  public getAttachment(chatId: number, messageId: number, attachmentId: number): Observable<Blob> {
    return this.httpService.get(`chats/${chatId}/messages/${messageId}/attachments/${attachmentId}`, {responseType: 'blob'});
  }
}
