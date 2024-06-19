import {RoleDto} from '../models/RoleDto';
import {UserDto} from '../models/UserDto';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {AttemptDto, FeatureDto, LessonDto, NotificationDto, StudentRankingDto, TaskDto, TopicDto} from './atom2024backend-dto';
import {AttemptStatus} from './atom2024backend-enums';
import {ChatDto, MessageDto} from './dto-chat';
import {TableLazyLoadEvent} from 'primeng/table';
import {PageResponse} from './query-lazy';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
providedIn:'root'
})
export class AttemptService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public finishAttempt(attemptId: number, attemptDto: AttemptDto): Observable<AttemptDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<AttemptDto>('attempts/' + attemptId + '', JSON.stringify(attemptDto) , {headers, responseType: 'json'});
  }

 public getAttempt(attemptId: number): Observable<AttemptDto>  {
    return this.httpService.get<AttemptDto>('attempts/' + attemptId + '', {responseType: 'json'});
  }

 public getAttempts(userId: number | null, status: AttemptStatus | null): Observable<AttemptDto[]>  {
    const queryParamsList: { name: string, value: string }[] = [];
    if (userId !== undefined && userId !== null) {
      queryParamsList.push({name: 'userId', value: userId.toString()});
    }

    if (status !== undefined && status !== null) {
      queryParamsList.push({name: 'status', value: status.toString()});
    }
    let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<AttemptDto[]>('attempts', {params, responseType: 'json'});
  }

 public getLastAttempt(topicId: number, lessonId: number, taskId: number): Observable<AttemptDto>  {
    return this.httpService.get<AttemptDto>('attempts/topics/' + topicId + '/lessons/' + lessonId + '/tasks/' + taskId + '', {responseType: 'json'});
  }

 public setTutorMark(attemptId: number, attemptDto: AttemptDto): Observable<AttemptDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.patch<AttemptDto>('attempts/' + attemptId + '', JSON.stringify(attemptDto) , {headers, responseType: 'json'});
  }

 public startNewAttempt(topicId: number, lessonId: number, taskId: number): Observable<AttemptDto>  {
    return this.httpService.post<AttemptDto>('attempts/topics/' + topicId + '/lessons/' + lessonId + '/tasks/' + taskId + '', null , {responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class ChatService {
  httpService: HttpClient;


 public addMessage(chatId: number, messageDto: MessageDto): Observable<void>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<void>('chats/' + chatId + '', JSON.stringify(messageDto) , {headers});
  }

 public addUserToChat(chatId: number, userId: number): Observable<void>  {
    return this.httpService.post<void>('chats/' + chatId + '/users/' + userId + '', null );
  }

 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createChat(chatDto: ChatDto): Observable<ChatDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<ChatDto>('chats', JSON.stringify(chatDto) , {headers, responseType: 'json'});
  }

 public getChat(chatId: number): Observable<ChatDto>  {
    return this.httpService.get<ChatDto>('chats/' + chatId + '', {responseType: 'json'});
  }

 public getChats(): Observable<ChatDto[]>  {
    return this.httpService.get<ChatDto[]>('chats', {responseType: 'json'});
  }

 public getUsers(): Observable<UserDto[]>  {
    return this.httpService.get<UserDto[]>('chats/all-users', {responseType: 'json'});
  }

 public leaveChat(chatId: number): Observable<void>  {
    return this.httpService.delete<void>('chats/' + chatId + '/leave');
  }

}

@Injectable({
providedIn:'root'
})
export class FeatureService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public getFeatures(): Observable<FeatureDto[]>  {
    return this.httpService.get<FeatureDto[]>('features', {responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class LessonService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

}

@Injectable({
providedIn:'root'
})
export class NotificationService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public getNewNotifications(lastReadNotificationId: number): Observable<NotificationDto[]>  {
    const queryParamsList: { name: string, value: string }[] = [];
    queryParamsList.push({name: 'lastReadNotificationId', value: lastReadNotificationId.toString()});
      let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<NotificationDto[]>('notifications', {params, responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class StatisticsService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public getStudentRankings(onSum: boolean, topicId: number | null): Observable<StudentRankingDto[]>  {
    const queryParamsList: { name: string, value: string }[] = [];
    queryParamsList.push({name: 'onSum', value: onSum.toString()});
  
    if (topicId !== undefined && topicId !== null) {
      queryParamsList.push({name: 'topicId', value: topicId.toString()});
    }
    let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<StudentRankingDto[]>('statistics/students', {params, responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class TaskService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public getRecommendations(lessonId: number): Observable<LessonDto[]>  {
    const queryParamsList: { name: string, value: string }[] = [];
    queryParamsList.push({name: 'lessonId', value: lessonId.toString()});
      let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<LessonDto[]>('tasks', {params, responseType: 'json'});
  }

 public getTasksWithStats(): Observable<TaskDto[]>  {
    return this.httpService.get<TaskDto[]>('tasks/stats', {responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class TopicService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public getTopicLessons(topicId: number): Observable<LessonDto[]>  {
    return this.httpService.get<LessonDto[]>('topics/' + topicId + '/lessons', {responseType: 'json'});
  }

 public getTopicLessonsWithLastAttempts(topicId: number): Observable<LessonDto[]>  {
    return this.httpService.get<LessonDto[]>('topics/' + topicId + '/lessons/attempts', {responseType: 'json'});
  }

 public getTopics(): Observable<TopicDto[]>  {
    return this.httpService.get<TopicDto[]>('topics', {responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class UserAccountService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public updateUser(userId: number, password: string, userDto: UserDto): Observable<UserDto>  {
    const queryParamsList: { name: string, value: string }[] = [];
    queryParamsList.push({name: 'password', value: password});
      let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<UserDto>('' + userId + '/account', JSON.stringify(userDto) , {headers, params, responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class UserAdminService {
  httpService: HttpClient;


 public archiveUser(userId: number): Observable<UserDto>  {
    return this.httpService.delete<UserDto>('administration/users/' + userId + '/archive', {responseType: 'json'});
  }

 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createUser(userDto: UserDto): Observable<UserDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<UserDto>('administration/users', JSON.stringify(userDto) , {headers, responseType: 'json'});
  }

 public getRoles(): Observable<RoleDto[]>  {
    return this.httpService.get<RoleDto[]>('administration/users/roles', {responseType: 'json'});
  }

 public getUser(userId: number, joinRoles: boolean = false): Observable<UserDto>  {
    const queryParamsList: { name: string, value: string }[] = [];
    if (joinRoles !== undefined && joinRoles !== null) {
      queryParamsList.push({name: 'joinRoles', value: joinRoles.toString()});
    }
    let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<UserDto>('administration/users/' + userId + '', {params, responseType: 'json'});
  }

 public getUsers(joinRoles: boolean = false): Observable<UserDto[]>  {
    const queryParamsList: { name: string, value: string }[] = [];
    if (joinRoles !== undefined && joinRoles !== null) {
      queryParamsList.push({name: 'joinRoles', value: joinRoles.toString()});
    }
    let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<UserDto[]>('administration/users', {params, responseType: 'json'});
  }

 public resetPassword(userId: number): Observable<UserDto>  {
    return this.httpService.patch<UserDto>('administration/users/' + userId + '', null , {responseType: 'json'});
  }

 public searchUsers(pageQuery: TableLazyLoadEvent): Observable<PageResponse<UserDto> >  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<PageResponse<UserDto> >('administration/users/search', JSON.stringify(pageQuery) , {headers, responseType: 'json'});
  }

 public unarchiveUser(userId: number): Observable<UserDto>  {
    return this.httpService.put<UserDto>('administration/users/' + userId + '/archive', null , {responseType: 'json'});
  }

 public updateUser(userId: number, userDto: UserDto): Observable<UserDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<UserDto>('administration/users/' + userId + '', JSON.stringify(userDto) , {headers, responseType: 'json'});
  }

}

