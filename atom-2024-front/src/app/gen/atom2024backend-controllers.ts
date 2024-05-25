import {RoleDto} from '../models/RoleDto';
import {UserDto} from '../models/UserDto';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {CourseDto, StudentDto, StudentInGroupDto, StudyGroupDto, TutorDto, TutorWithCourseDto} from './atom2024backend-dto';
import {TableLazyLoadEvent} from 'primeng/table';
import {PageResponse} from './query-lazy';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
providedIn:'root'
})
export class CourseService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createCourse(courseDto: CourseDto): Observable<CourseDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<CourseDto>('courses', JSON.stringify(courseDto) , {headers, responseType: 'json'});
  }

 public getCourse(courseId: number): Observable<CourseDto>  {
    return this.httpService.get<CourseDto>('courses/' + courseId + '', {responseType: 'json'});
  }

 public getCourses(): Observable<CourseDto[]>  {
    return this.httpService.get<CourseDto[]>('courses', {responseType: 'json'});
  }

 public searchCourses(pageQuery: TableLazyLoadEvent): Observable<PageResponse<CourseDto> >  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<PageResponse<CourseDto> >('courses/search', JSON.stringify(pageQuery) , {headers, responseType: 'json'});
  }

 public updateCourse(courseId: number, courseDto: CourseDto): Observable<CourseDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<CourseDto>('courses/' + courseId + '', JSON.stringify(courseDto) , {headers, responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class StudentService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createStudent(studentDto: StudentDto): Observable<StudentDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<StudentDto>('students', JSON.stringify(studentDto) , {headers, responseType: 'json'});
  }

 public getStudent(studentId: number): Observable<StudentDto>  {
    return this.httpService.get<StudentDto>('students/' + studentId + '', {responseType: 'json'});
  }

 public getStudents(): Observable<StudentDto[]>  {
    return this.httpService.get<StudentDto[]>('students', {responseType: 'json'});
  }

 public searchStudents(pageQuery: TableLazyLoadEvent): Observable<PageResponse<StudentDto> >  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<PageResponse<StudentDto> >('students/search', JSON.stringify(pageQuery) , {headers, responseType: 'json'});
  }

 public updateStudent(studentId: number, studentDto: StudentDto): Observable<StudentDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<StudentDto>('students/' + studentId + '', JSON.stringify(studentDto) , {headers, responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class StudyGroupService {
  httpService: HttpClient;


 public addStudent(studyGroupId: number, studentId: number): Observable<StudentInGroupDto>  {
    return this.httpService.post<StudentInGroupDto>('study-groups/' + studyGroupId + '/students/' + studentId + '', null , {responseType: 'json'});
  }

 public addTutor(studyGroupId: number, tutorId: number, courseId: number): Observable<TutorWithCourseDto>  {
    return this.httpService.post<TutorWithCourseDto>('study-groups/' + studyGroupId + '/tutors/' + tutorId + '/' + courseId + '', null , {responseType: 'json'});
  }

 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createStudyGroup(studyGroupDto: StudyGroupDto): Observable<StudyGroupDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<StudyGroupDto>('study-groups', JSON.stringify(studyGroupDto) , {headers, responseType: 'json'});
  }

 public getStudent(studyGroupId: number, studentId: number): Observable<StudentInGroupDto>  {
    return this.httpService.get<StudentInGroupDto>('study-groups/' + studyGroupId + '/students/' + studentId + '', {responseType: 'json'});
  }

 public getStudents(studyGroupId: number): Observable<StudentInGroupDto[]>  {
    return this.httpService.get<StudentInGroupDto[]>('study-groups/' + studyGroupId + '/students', {responseType: 'json'});
  }

 public getStudyGroup(studyGroupId: number): Observable<StudyGroupDto>  {
    return this.httpService.get<StudyGroupDto>('study-groups/' + studyGroupId + '', {responseType: 'json'});
  }

 public getStudyGroups(): Observable<StudyGroupDto[]>  {
    return this.httpService.get<StudyGroupDto[]>('study-groups', {responseType: 'json'});
  }

 public getTutor(studyGroupId: number, tutorId: number, courseId: number): Observable<TutorWithCourseDto>  {
    return this.httpService.get<TutorWithCourseDto>('study-groups/' + studyGroupId + '/tutors/' + tutorId + '/' + courseId + '', {responseType: 'json'});
  }

 public getTutors(studyGroupId: number): Observable<TutorWithCourseDto[]>  {
    return this.httpService.get<TutorWithCourseDto[]>('study-groups/' + studyGroupId + '/tutors', {responseType: 'json'});
  }

 public removeStudent(studyGroupId: number, studentId: number): Observable<void>  {
    return this.httpService.delete<void>('study-groups/' + studyGroupId + '/students/' + studentId + '');
  }

 public removeTutor(studyGroupId: number, tutorId: number, courseId: number): Observable<void>  {
    return this.httpService.delete<void>('study-groups/' + studyGroupId + '/tutors/' + tutorId + '/' + courseId + '');
  }

 public searchStudents(studyGroupId: number, pageQuery: TableLazyLoadEvent): Observable<PageResponse<StudentInGroupDto> >  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<PageResponse<StudentInGroupDto> >('study-groups/' + studyGroupId + '/students/search', JSON.stringify(pageQuery) , {headers, responseType: 'json'});
  }

 public searchStudyGroups(pageQuery: TableLazyLoadEvent): Observable<PageResponse<StudyGroupDto> >  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<PageResponse<StudyGroupDto> >('study-groups/search', JSON.stringify(pageQuery) , {headers, responseType: 'json'});
  }

 public searchTutors(studyGroupId: number, pageQuery: TableLazyLoadEvent): Observable<PageResponse<TutorWithCourseDto> >  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<PageResponse<TutorWithCourseDto> >('study-groups/' + studyGroupId + '/tutors/search', JSON.stringify(pageQuery) , {headers, responseType: 'json'});
  }

 public updateStudyGroup(studyGroupId: number, studyGroupDto: StudyGroupDto): Observable<StudyGroupDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<StudyGroupDto>('study-groups/' + studyGroupId + '', JSON.stringify(studyGroupDto) , {headers, responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class TutorService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createTutor(tutorDto: TutorDto): Observable<TutorDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<TutorDto>('tutors', JSON.stringify(tutorDto) , {headers, responseType: 'json'});
  }

 public getTutor(tutorId: number): Observable<TutorDto>  {
    return this.httpService.get<TutorDto>('tutors/' + tutorId + '', {responseType: 'json'});
  }

 public getTutors(): Observable<TutorDto[]>  {
    return this.httpService.get<TutorDto[]>('tutors', {responseType: 'json'});
  }

 public searchTutors(pageQuery: TableLazyLoadEvent): Observable<PageResponse<TutorDto> >  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<PageResponse<TutorDto> >('tutors/search', JSON.stringify(pageQuery) , {headers, responseType: 'json'});
  }

 public updateTutor(tutorId: number, tutorDto: TutorDto): Observable<TutorDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<TutorDto>('tutors/' + tutorId + '', JSON.stringify(tutorDto) , {headers, responseType: 'json'});
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

