import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {SurveyAttemptAnswerDto, SurveyAttemptDto, SurveyDto, SurveyQuestionDto} from './survey-dto';

@Injectable({
providedIn:'root'
})
export class SurveyAttemptService {
  httpService: HttpClient;


 public beginSurveyAttempt(surveyId: number): Observable<SurveyAttemptDto>  {
    return this.httpService.post<SurveyAttemptDto>('surveys/' + surveyId + '/attempts', null , {responseType: 'json'});
  }

 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public finishAttempt(surveyId: number, surveyAttemptId: number, attemptDto: SurveyAttemptDto): Observable<SurveyAttemptDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<SurveyAttemptDto>('surveys/' + surveyId + '/attempts/' + surveyAttemptId + '', JSON.stringify(attemptDto) , {headers, responseType: 'json'});
  }

 public getAttemptAnswers(surveyId: number, surveyAttemptId: number): Observable<SurveyAttemptAnswerDto[]>  {
    return this.httpService.get<SurveyAttemptAnswerDto[]>('surveys/' + surveyId + '/attempts/' + surveyAttemptId + '/answers', {responseType: 'json'});
  }

 public getSurveyAttempt(surveyId: number, surveyAttemptId: number): Observable<SurveyAttemptDto>  {
    return this.httpService.get<SurveyAttemptDto>('surveys/' + surveyId + '/attempts/' + surveyAttemptId + '', {responseType: 'json'});
  }

 public saveAnswer(surveyId: number, surveyAttemptId: number, answerDto: SurveyAttemptAnswerDto): Observable<void>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<void>('surveys/' + surveyId + '/attempts/' + surveyAttemptId + '/answers', JSON.stringify(answerDto) , {headers});
  }

}

@Injectable({
providedIn:'root'
})
export class SurveyQuestionService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public getSurveyQuestions(surveyId: number): Observable<SurveyQuestionDto[]>  {
    return this.httpService.get<SurveyQuestionDto[]>('surveys/' + surveyId + '/questions', {responseType: 'json'});
  }

 public getSurveyQuestionsWithAnswers(surveyId: number): Observable<SurveyQuestionDto[]>  {
    return this.httpService.get<SurveyQuestionDto[]>('surveys/' + surveyId + '/questions/answers', {responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class SurveyService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createSurvey(surveyDto: SurveyDto): Observable<SurveyDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<SurveyDto>('surveys', JSON.stringify(surveyDto) , {headers, responseType: 'json'});
  }

 public deleteSurvey(surveyId: number): Observable<void>  {
    return this.httpService.delete<void>('surveys/' + surveyId + '');
  }

 public getSurvey(surveyId: number): Observable<SurveyDto>  {
    return this.httpService.get<SurveyDto>('surveys/' + surveyId + '', {responseType: 'json'});
  }

 public getSurveys(): Observable<SurveyDto[]>  {
    return this.httpService.get<SurveyDto[]>('surveys', {responseType: 'json'});
  }

 public updateSurvey(surveyId: number, surveyDto: SurveyDto): Observable<SurveyDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<SurveyDto>('surveys/' + surveyId + '', JSON.stringify(surveyDto) , {headers, responseType: 'json'});
  }

}

