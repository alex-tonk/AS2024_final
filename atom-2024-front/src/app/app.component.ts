import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterOutlet} from '@angular/router';
import {LoaderComponent} from "./components/common/loader/loader.component";
import {HeaderComponent} from "./components/header/header.component";
import {UserService} from "./services/user.service";
import {ToastModule} from "primeng/toast";
import {ConfirmationService, MessageService, PrimeNGConfig} from "primeng/api";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {ConfirmPopupModule} from "primeng/confirmpopup";
import {Translation} from "./components/common/locale/Translation";
import {BlockUIModule} from "primeng/blockui";
import {ExportTable} from "./components/common/table/ExportTable";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, LoaderComponent, HeaderComponent, ToastModule, ConfirmDialogModule, ConfirmPopupModule, BlockUIModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  providers: [ConfirmationService]
})
export class AppComponent implements OnInit {
  title = 'atom-2024-front';
  loading = false;

  constructor(private userService: UserService,
              private config: PrimeNGConfig,
              private messageService: MessageService) {
    this.config.setTranslation(Translation.ru())
  }

  async ngOnInit() {
    this.loading = true;
    try {
      ExportTable.messageSerivce = this.messageService;
      await new Promise(resolve => setTimeout(resolve, 100));
    } finally {
      this.loading = false;
    }
  }

  onToastButtonClick() {
    this.messageService.clear(MessageServiceKey.OK)
  }

  getToastIconClass(message: any) {
    if (!message) {
      return 'pi pi-check';
    }
    switch (message.severity) {
      case 'success':
        return 'pi pi-check-circle';
      case 'warn':
        return 'pi pi-exclamation-triangle';
      case 'error':
        return 'pi pi-ban';
      case 'info':
        return 'pi pi-info-circle';
      default:
        return 'pi pi-check';
    }
  }

  getSeverity(message: any) {
    if (!message) {
      return 'secondary';
    }
    switch (message.severity) {
      case 'success':
        return 'success';
      case 'warn':
        return 'warning';
      case 'error':
        return 'danger';
      case 'info':
        return 'info';
      default:
        return 'secondary';
    }
  }

  protected readonly MessageServiceKey = MessageServiceKey;
}

export enum MessageServiceKey {
  OK = 'MESSAGE_SERVICE_OK_KEY'
}
