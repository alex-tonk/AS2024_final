import {Injectable} from '@angular/core';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FakeService {
  getTestTypes(): Observable<any[]> {
    return new Observable((sub) => {
      sub.next([
        {
          id: 1,
          name: 'Растяжение'
        },
        {
          id: 2,
          name: 'Разрыв'
        },
        {
          id: 3,
          name: 'Изгиб'
        },
        {
          id: 4,
          name: 'Климатическое испытание'
        }
      ])
      sub.complete();
    })
  }

  getVirtualTests(): Observable<any[]> {
    return new Observable((sub) => {
      sub.next([
        {
          id: 1,
          type: 'Растяжение',
          name: 'Эмуляция растяжения',
          variables: 'META_1, META_2',
          code: ''
        },
        {
          id: 2,
          type: 'Разрыв',
          name: 'Эмуляция разрыва',
          variables: 'META_1',
          code: ''
        },
        {
          id: 3,
          type: 'Изгиб',
          name: 'Эмуляция изгиба',
          variables: 'META_1, META_2',
          code: ''
        },
        {
          id: 4,
          type: 'Климатическое',
          name: 'Эмуляция всемирного потепления',
          variables: 'META_1, META_2, META_3',
          code: ''
        }
      ])
      sub.complete();
    })
  }

  getVirtualTestById(id: number): Observable<any> {
    return new Observable((sub) => {
      sub.next(
        [
          {
            id: 1,
            type: {id: 1, name: 'Растяжение'},
            name: 'Эмуляция растяжения',
            variables: 'META_1, META_2',
            code: ''
          },
          {
            id: 2,
            type: {id: 2, name: 'Разрыв'},
            name: 'Эмуляция разрыва',
            variables: 'META_1',
            code: ''
          },
          {
            id: 3,
            type: {id: 3, name: 'Изгиб'},
            name: 'Эмуляция изгиба',
            variables: 'META_1, META_2',
            code: ''
          },
          {
            id: 4,
            type: {id: 4, name: 'Климатическое'},
            name: 'Эмуляция всемирного потепления',
            variables: 'META_1, META_2, META_3',
            code: ''
          }
        ].find(t => t.id === id)
      )
      sub.complete();
    })
  }

  getTests(): Observable<any[]> {
    return new Observable((sub) => {
      sub.next(
        [
          {
            id: 1,
            name: 'СТАНОК 1',
            description: 'Лучшая тачка',
            isBusy: false,
            showResult: false,
            isStopped: false
          },
          {
            id: 2,
            name: 'СТАНОК 2',
            description: 'Занятая тачка',
            isBusy: true,
            showResult: false,
            isStopped: false
          },
          {
            id: 3,
            name: 'СТАНОК 3',
            description: 'Ошибочная тачка',
            isBusy: false,
            showResult: false,
            isStopped: false,
            hasError: true
          }, {
            id: 4,
            name: 'СТАНОК 4',
            description: 'Остановленная тачка',
            isBusy: false,
            showResult: false,
            isStopped: true
          }
        ]
      )
      sub.complete();
    })
  }

  createTest(test: any): Observable<any> {
    return new Observable((sub) => {
      sub.next(test);
      sub.complete();
    });
  }
}
