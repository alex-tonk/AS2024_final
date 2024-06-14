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

  public uploadChatFile(file: File): Observable<AttachmentDto> {
    let formData = new FormData();
    formData.append('file', file);
    return this.httpService.post<AttachmentDto>('chats/attachments', formData, {responseType: 'json'});
  }

  public deleteChatFile(fileId: number): Observable<void> {
    return this.httpService.delete<void>('chats/attachments/' + fileId);
  }

  public getChatAttachment(chatId: number, messageId: number, attachmentId: number): Observable<Blob> {
    return this.httpService.get(`chats/${chatId}/messages/${messageId}/attachments/${attachmentId}`, {responseType: 'blob'});
  }

  public uploadSurveyQuestionFile(file: File): Observable<number> {
    let formData = new FormData();
    formData.append('file', file);
    return this.httpService.post<number>('surveys/files', formData, {responseType: 'json'});
  }

  public deleteSurveyQuestionFile(fileId: number): Observable<boolean> {
    return this.httpService.delete<boolean>(`surveys/files/${fileId}`);
  }

  public getSurveyQuestionFile(fileId: number): Observable<Blob> {
    return this.httpService.get(`surveys/files/${fileId}`, {responseType: 'blob'});
  }
}
