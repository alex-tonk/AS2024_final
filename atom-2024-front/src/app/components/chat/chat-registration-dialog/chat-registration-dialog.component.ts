import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DialogModule} from 'primeng/dialog';
import {PaginatorModule} from 'primeng/paginator';
import {ChipsModule} from 'primeng/chips';
import {ButtonModule} from 'primeng/button';
import {MultiSelectModule} from 'primeng/multiselect';
import {ChatService, UserAdminService} from '../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {UserDto} from '../../../models/UserDto';
import {ChatDto} from '../../../gen/dto-chat';
import {SelectButtonModule} from 'primeng/selectbutton';
import {ChatType} from '../../../gen/entities-enums';
import {UserService} from '../../../services/user.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-chat-registration-dialog',
  standalone: true,
  imports: [
    DialogModule,
    PaginatorModule,
    ChipsModule,
    ButtonModule,
    MultiSelectModule,
    SelectButtonModule,
    NgIf
  ],
  templateUrl: './chat-registration-dialog.component.html',
  styleUrl: './chat-registration-dialog.component.css'
})
export class ChatRegistrationDialogComponent implements OnInit {
  @Input() loading = false;
  @Output() result: EventEmitter<ChatDto | null> = new EventEmitter<ChatDto | null>();

  visible = true;
  allUsers: UserDto[] = [];
  selectedUser?: UserDto;

  chat = new ChatDto();

  typeOptions = [
    {label: 'Личный', value: ChatType.DIALOGUE},
    {label: 'Групповой', value: ChatType.PUBLIC}
  ];

  constructor(
    private chatService: ChatService,
    private userService: UserService) {
  }

  async ngOnInit() {
    this.chat.type = ChatType.DIALOGUE;
    this.allUsers = await lastValueFrom(this.chatService.getUsers());
    this.allUsers = this.allUsers.filter(u => u.id != this.userService.user?.id);
  }

  create() {
    if (this.chat.type === ChatType.DIALOGUE) {
      this.chat.name = this.selectedUser?.fullName;
      this.chat.members = [this.selectedUser!];
    }
    this.result.emit(this.chat)
  }

  onTypeChange() {
    this.chat.name = undefined;
    this.chat.members = undefined;
    this.selectedUser = undefined;
  }

  onHide() {
    this.result.emit(null);
  }

  protected readonly ChatType = ChatType;
}
