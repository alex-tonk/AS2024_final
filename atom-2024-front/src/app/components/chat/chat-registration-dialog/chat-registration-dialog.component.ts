import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DialogModule} from 'primeng/dialog';
import {PaginatorModule} from 'primeng/paginator';
import {ChipsModule} from 'primeng/chips';
import {ButtonModule} from 'primeng/button';
import {MultiSelectModule} from 'primeng/multiselect';
import {UserAdminService} from '../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {UserDto} from '../../../models/UserDto';
import {ChatDto} from '../../../gen/dto-chat';

@Component({
  selector: 'app-chat-registration-dialog',
  standalone: true,
  imports: [
    DialogModule,
    PaginatorModule,
    ChipsModule,
    ButtonModule,
    MultiSelectModule
  ],
  templateUrl: './chat-registration-dialog.component.html',
  styleUrl: './chat-registration-dialog.component.css'
})
export class ChatRegistrationDialogComponent implements OnInit {
  @Input() loading = false;
  @Output() result: EventEmitter<ChatDto | null> = new EventEmitter<ChatDto | null>();


  options: UserDto[] = [];

  chat = new ChatDto();

  constructor(private userAdminService: UserAdminService) {
  }

  async ngOnInit() {
    this.options = await lastValueFrom(this.userAdminService.getUsers());
  }
}
