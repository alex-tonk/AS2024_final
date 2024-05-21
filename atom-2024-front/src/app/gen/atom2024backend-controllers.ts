import {RoleDto} from '../models/RoleDto';
import {UserDto} from '../models/UserDto';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {ProductDto, StandDto, StandEndpointDto, StandEndpointTypeDto, TestDto, TestGroupDto} from './atom2024backend-dto';
import {TestGroupFilterEnum} from './dto-enums';
import {TableLazyLoadEvent} from 'primeng/table';
import {PageResponse} from './query-lazy';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
providedIn:'root'
})
export class ProductService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public getProduct(id: number): Observable<ProductDto>  {
    return this.httpService.get<ProductDto>('products/' + id + '', {responseType: 'json'});
  }

 public getProducts(): Observable<ProductDto[]>  {
    return this.httpService.get<ProductDto[]>('products', {responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class StandService {
  httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public createVirtualStandEndpoint(standEndpointDto: StandEndpointDto): Observable<StandEndpointDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<StandEndpointDto>('stands', JSON.stringify(standEndpointDto) , {headers, responseType: 'json'});
  }

 public getAllStandEndpoints(): Observable<StandEndpointDto[]>  {
    return this.httpService.get<StandEndpointDto[]>('stands/endpoints', {responseType: 'json'});
  }

 public getStand(id: number): Observable<StandDto>  {
    return this.httpService.get<StandDto>('stands/' + id + '', {responseType: 'json'});
  }

 public getStandEndpoint(id: number, endpointId: number): Observable<StandEndpointDto>  {
    return this.httpService.get<StandEndpointDto>('stands/' + id + '/endpoints/' + endpointId + '', {responseType: 'json'});
  }

 public getStandEndpointTypes(): Observable<StandEndpointTypeDto[]>  {
    return this.httpService.get<StandEndpointTypeDto[]>('stands/types', {responseType: 'json'});
  }

 public getStandEndpoints(id: number): Observable<StandEndpointDto[]>  {
    return this.httpService.get<StandEndpointDto[]>('stands/' + id + '/endpoints', {responseType: 'json'});
  }

 public getStands(): Observable<StandDto[]>  {
    return this.httpService.get<StandDto[]>('stands', {responseType: 'json'});
  }

 public getVirtualEndpoints(): Observable<StandEndpointDto[]>  {
    return this.httpService.get<StandEndpointDto[]>('stands/endpoints/virtual', {responseType: 'json'});
  }

 public updateVirtualStandEndpoint(standId: number, standEndpointId: number, standEndpointDto: StandEndpointDto): Observable<StandEndpointDto>  {
    const queryParamsList: { name: string, value: string }[] = [];
    queryParamsList.push({name: 'standId', value: standId.toString()});
  
    queryParamsList.push({name: 'standEndpointId', value: standEndpointId.toString()});
      let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<StandEndpointDto>('stands', JSON.stringify(standEndpointDto) , {headers, params, responseType: 'json'});
  }

}

@Injectable({
providedIn:'root'
})
export class TestGroupService {
  httpService: HttpClient;


 public cancelTest(id: number): Observable<TestGroupDto>  {
    return this.httpService.delete<TestGroupDto>('test-groups/' + id + '', {responseType: 'json'});
  }

 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
  }

 public copyTestGroup(testGroupDto: TestGroupDto): Observable<TestGroupDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.put<TestGroupDto>('test-groups', JSON.stringify(testGroupDto) , {headers, responseType: 'json'});
  }

 public createTestGroup(testGroupDto: TestGroupDto): Observable<TestGroupDto>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<TestGroupDto>('test-groups', JSON.stringify(testGroupDto) , {headers, responseType: 'json'});
  }

 public getTest(id: number, testId: number): Observable<TestDto>  {
    return this.httpService.get<TestDto>('test-groups/' + id + '/tests/' + testId + '', {responseType: 'json'});
  }

 public getTestGroup(id: number): Observable<TestGroupDto>  {
    return this.httpService.get<TestGroupDto>('test-groups/' + id + '', {responseType: 'json'});
  }

 public getTestGroups(filterEnum: TestGroupFilterEnum = TestGroupFilterEnum.ALL): Observable<TestGroupDto[]>  {
    const queryParamsList: { name: string, value: string }[] = [];
    if (filterEnum !== undefined && filterEnum !== null) {
      queryParamsList.push({name: 'filterEnum', value: filterEnum.toString()});
    }
    let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<TestGroupDto[]>('test-groups', {params, responseType: 'json'});
  }

 public getTests(id: number): Observable<TestDto[]>  {
    return this.httpService.get<TestDto[]>('test-groups/' + id + '/tests', {responseType: 'json'});
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

 public getUser(userId: number, joinRoles: boolean = false, joinAvailableEndpoints: boolean = false): Observable<UserDto>  {
    const queryParamsList: { name: string, value: string }[] = [];
    if (joinRoles !== undefined && joinRoles !== null) {
      queryParamsList.push({name: 'joinRoles', value: joinRoles.toString()});
    }

    if (joinAvailableEndpoints !== undefined && joinAvailableEndpoints !== null) {
      queryParamsList.push({name: 'joinAvailableEndpoints', value: joinAvailableEndpoints.toString()});
    }
    let params = new HttpParams();
    for (const queryParam of queryParamsList) {
      params = params.append(queryParam.name, queryParam.value);
    }
    return this.httpService.get<UserDto>('administration/users/' + userId + '', {params, responseType: 'json'});
  }

 public getUsers(joinRoles: boolean = false, joinAvailableStandEndpoints: boolean = false): Observable<UserDto[]>  {
    const queryParamsList: { name: string, value: string }[] = [];
    if (joinRoles !== undefined && joinRoles !== null) {
      queryParamsList.push({name: 'joinRoles', value: joinRoles.toString()});
    }

    if (joinAvailableStandEndpoints !== undefined && joinAvailableStandEndpoints !== null) {
      queryParamsList.push({name: 'joinAvailableStandEndpoints', value: joinAvailableStandEndpoints.toString()});
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

