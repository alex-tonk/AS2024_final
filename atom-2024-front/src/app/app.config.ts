import {APP_INITIALIZER, ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {ApiInterceptor} from "./services/interceptors/api-interceptor";
import {AuthInterceptor} from "./services/interceptors/auth-interceptor";
import {provideAnimations} from "@angular/platform-browser/animations";
import {UserService} from "./services/user.service";
import {MessageService} from "primeng/api";
import {DateInterceptor} from "./services/interceptors/date-interceptor";
import {ConfigService} from './services/config.service';
import {NGX_MONACO_EDITOR_CONFIG} from "ngx-monaco-editor-v2";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi()),
    MessageService,
    {
      provide: APP_INITIALIZER,
      multi: true,
      deps: [ConfigService, UserService],
      useFactory: (configService: ConfigService, userService: UserService) => () => configService.loadConfig()
        .then(() => userService.init())
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: DateInterceptor,
      multi: true
    },
    {
      provide: NGX_MONACO_EDITOR_CONFIG,
      useFactory: () => {
        return {
          defaultOptions: {
            language: "javascript",
            scrollBeyondLastLine: false,
            minimap: {
              enabled: true
            },
            scrollbar: {
              verticalHasArrows: true,
              horizontalHasArrows: true,
              verticalScrollbarSize: 14,
              horizontalScrollbarSize: 14
            }
          }
        }
      }
    }
  ]
};
