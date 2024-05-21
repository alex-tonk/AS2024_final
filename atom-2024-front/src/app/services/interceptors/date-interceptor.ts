import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable()
export class DateInterceptor implements HttpInterceptor {
  dateRegEx = /^((\d{4}-\d\d-\d\d)|(\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(\.\d+)?(([+-]\d\d:\d\d)|Z)?))$/;

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(map((event: HttpEvent<any>) => {
      if (event instanceof HttpResponse && req.responseType == 'json') {
        this.convertToDate( event.body, '', new Map<string, string>());
        event = event.clone({body: event.body});
      }
      return event;
    }));
  }

  convertToDate(body: any, path: string, typesCache: Map<string, string>) {
    if (body === null || body === undefined) {
      return;
    }

    if (typeof body !== 'object') {
      return;
    }

    for (const key of Object.keys(body)) {
      const value = body[key];
      if (value == null) {
        continue;
      }
      const curPath = path + '.' + (Array.isArray(body) ? '' : key);
      if (typesCache.has(curPath)) {
        if (typesCache.get(curPath) == 'date') {
          body[key] = new Date(value);
        } else if (typesCache.get(curPath) == 'object') {
          this.convertToDate(value, curPath, typesCache);
        }
      } else if (!(key == 'value' && body.type) && this.dateRegEx.test(value)) {
        body[key] = new Date(value);
        typesCache.set(curPath, 'date');
      } else if (typeof value === 'object') {
        this.convertToDate(value, curPath, typesCache);
        typesCache.set(curPath, 'object');
      } else {
        typesCache.set(curPath, 'other');
      }
    }
  }
}

