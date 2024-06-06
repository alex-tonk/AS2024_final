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
import {TooltipModule} from 'primeng/tooltip';
import {DialogService} from 'primeng/dynamicdialog';
import {Listbox} from 'primeng/listbox';
import {ScrollerLazyLoadEvent, ScrollerModule} from 'primeng/scroller';
import {TableLazyLoadEvent} from 'primeng/table';

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
    TooltipModule,
    ScrollerModule
  ],
  providers: [DialogService],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit, OnDestroy {
  @ViewChild('bottomAnchor') bottomAnchor: ElementRef;

  registrationDialogVisible = false;
  registrationDialogLoading = false;

  chats: ChatDto[] = [];
  chat?: ChatDto;
  newMessage: MessageDto = new MessageDto();

  loading = false;
  chatsLoading = false;

  lastPageQuery?: TableLazyLoadEvent;

  private reloadInterval: number;

  isNotMyMessage = (message: MessageDto) => message.author?.id !== this.userService.user?.id;

  constructor(private chatService: ChatService,
              private userService: UserService,
              private confirmationService: ConfirmationService,
              private dialogService: DialogService) {
  }

  async onKeyPress(event: KeyboardEvent) {
    if (event.key !== 'enter' || !this.newMessage.content || this.chat?.id == null) {
      return;
    }
    await this.sendMessage();
  }

  async reloadChat() {
    // let response = await lastValueFrom(this.chatService.searchChats(this.lastPageQuery!));
    // const newChats = response.items;
    // if (newChats.length !== this.chats.length || !newChats.reduce((p, c, i) => p && c.id === this.chats[i].id && c.lastMessage?.id === this.chats[i]?.lastMessage?.id, true)) {
    //   this.chats = newChats;
    // }
    // if (this.chat?.id == null) {
    //   return;
    // }
    // try {
    //   this.chat = await lastValueFrom(this.chatService.getChat(this.chat.id));
    // } catch (e) {
    //   this.chat = undefined;
    //   throw e;
    // }
  }

  async ngOnInit(): Promise<void> {
    this.chatsLoading = true;

    this.reloadInterval = setInterval(this.reloadChat.bind(this), 1000);
  }

  ngOnDestroy() {
    clearInterval(this.reloadInterval);
  }

  async onLoad(lazyLoadEvent: ScrollerLazyLoadEvent) {
    // lazyLoadEvent.last = 100;
    const pageQuery = {...lazyLoadEvent, rows: lazyLoadEvent.last - lazyLoadEvent.first};
    this.lastPageQuery = pageQuery;
    this.chatsLoading = true;
    try {
      let response = await lastValueFrom(this.chatService.searchChats(pageQuery));
      const {first, last} = lazyLoadEvent;
      const lazyItems = [...this.chats];

      for (let i = first; i < last; i++) {
        lazyItems[i] = response.items![i - first];
      }

      this.chats = lazyItems;
    } finally {
      this.chatsLoading = false;
    }
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
      this.registrationDialogVisible = false;
      return;
    }
    try {
      this.registrationDialogLoading = true;
      await lastValueFrom(this.chatService.createChat(event));
      await this.reloadChat();
      this.registrationDialogVisible = false;
    } finally {
      this.registrationDialogLoading = false;
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
      }
    });
  }

  showMembers() {
    let dynamicDialogRef = this.dialogService.open(Listbox, {
      modal: true,
      header: 'Участники чата'
    });
    dynamicDialogRef.onChildComponentLoaded.subscribe(value => {
      value.readonly = true;
      value.autoOptionFocus = false;
      value.optionLabel = 'fullName';
      value.options = this.chat?.members!;
    })
  }
}
