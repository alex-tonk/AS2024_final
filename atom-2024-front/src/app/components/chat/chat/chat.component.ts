import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {ChatDto, MessageDto} from '../../../gen/dto-chat';
import {ChatService} from '../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {UserService} from '../../../services/user.service';
import {FormsModule} from '@angular/forms';
import {ChipsModule} from 'primeng/chips';
import {ButtonModule} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {ChatRegistrationDialogComponent} from '../chat-registration-dialog/chat-registration-dialog.component';
import {ConfirmationService} from 'primeng/api';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    NgForOf,
    DatePipe,
    FormsModule,
    ChipsModule,
    ButtonModule,
    DialogModule,
    ChatRegistrationDialogComponent,
    NgIf,
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css',
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild('registrationDialogComponent') registrationDialogComponent: ChatRegistrationDialogComponent;
  @ViewChild('bottomAnchor') bottomAnchor: ElementRef;

  chats: ChatDto[] = [];
  chat?: ChatDto;
  newMessage: MessageDto = new MessageDto();

  loading = false;

  private reloadInterval: number;

  isNotMyMessage = (message: MessageDto) => message.author?.id !== this.userService.user?.id;

  constructor(private chatService: ChatService,
              private userService: UserService,
              private confirmationService: ConfirmationService) {
  }

  async onKeyPress(event: KeyboardEvent) {
    if (event.key !== 'enter' || !this.newMessage.content || this.chat?.id == null) {
      return;
    }
    await this.sendMessage();
  }

  async reloadChat() {
    const newChats = await lastValueFrom(this.chatService.getChats());
    if (newChats.length !== this.chats.length || !newChats.reduce((p, c, i) => p && c.id === this.chats[i].id && c.lastMessage?.id === this.chats[i]?.lastMessage?.id, true)) {
      this.chats = newChats;
    }
    if (this.chat?.id == null) {
      return;
    }
    try {
      this.chat = await lastValueFrom(this.chatService.getChat(this.chat.id));
    } catch (e) {
      this.chat = undefined;
      throw e;
    }
  }

  async ngOnInit(): Promise<void> {
    this.chats = await lastValueFrom(this.chatService.getChats());

    this.reloadInterval = setInterval(this.reloadChat.bind(this), 1000);
  }

  ngOnDestroy() {
    clearInterval(this.reloadInterval);
  }

  async sendMessage() {
    if (this.chat?.id == null) {
      return;
    }

    await lastValueFrom(this.chatService.addMessage(this.chat.id, this.newMessage));
    this.newMessage = new MessageDto();
    await this.reloadChat();
    this.scrollToBottom();
  }

  async createChat(event: ChatDto | null) {
    if (event == null) {
      this.registrationDialogComponent.visible = false;
      return;
    }
    try {
      this.registrationDialogComponent.loading = true;
      await lastValueFrom(this.chatService.createChat(event));
      await this.reloadChat();
      this.registrationDialogComponent.visible = false;
    } finally {
      this.registrationDialogComponent.loading = false;
    }
  }

  async selectChat(chat: ChatDto) {
    this.chat = await lastValueFrom(this.chatService.getChat(chat.id!));
    this.scrollToBottom('instant');
  }

  private scrollToBottom(behavior: 'auto' | 'instant' | 'smooth' = 'smooth') {
    setTimeout(() => this.bottomAnchor.nativeElement.scrollIntoView({behavior: behavior}));
  }

  leaveChat(chat: ChatDto) {
    if (chat?.id == null) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Покинуть чат?',
      message: 'Вы уверены, что хотите покинуть чат?',
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: async () => {
        if (chat?.id == null) {
          return;
        }
        try {
          await lastValueFrom(this.chatService.leaveChat(chat.id));
          if (this.chat?.id === chat.id) {
            this.chat = undefined;
          }
        } finally {
          this.chats = await lastValueFrom(this.chatService.getChats());
        }
      },
    });
  }
}
