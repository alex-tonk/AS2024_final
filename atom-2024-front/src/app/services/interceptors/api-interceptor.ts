import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {environment} from "../../../environments/environment";
import {MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {MessageServiceKey} from "../../app.component";
import {ConfigService} from "../config.service";

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
  readonly handledErrors = ['BusinessLogicException', 'InsufficientPrivilegesException']

  constructor(private configService: ConfigService,
              private messageService: MessageService,
              private router: Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!req.url.toLocaleUpperCase().startsWith('HTTP') && !req.url.includes('.json')) {
      req = req.clone({url: `${this.configService.baseUrl}/${req.url}`});
    }
    return next.handle(req).pipe(
      catchError((response: HttpErrorResponse) => {
        try {
          switch (response.status) {
            case 401: {
              this.showError('Ошибка ресурсного доступа');
              this.router.navigate(['/login'])
                .then(value => window.location.reload());
              break;
            }
            case 0: {
              this.showError('Бэкенд не запущен');
              break;
            }
            default: {
              const error = typeof response.error === 'string' ? JSON.parse(response.error) : response.error;
              if (this.handledErrors.includes(error.className)) {
                this.showError(error.message);
              } else {
                if (environment.production) {
                  console.log(`Непредусмотренная ошибка: ${response.message}`);
                } else {
                  this.showError(response.message);
                }
              }
            }
          }
        } catch (e) {
          if (environment.production) {
            console.log(`Не удалось распарсить ошибку: ${response.error}`);
          } else {
            this.showError(response.message);
          }
        }
        throw response;
      })
    );
  }

  showError(message: string) {
    this.messageService.add({
      key: MessageServiceKey.OK,
      sticky: true,
      severity: 'error',
      summary: 'Ошибка',
      detail: `${message ?? 'Что-то пошло не так'}`
    });
  }
}
