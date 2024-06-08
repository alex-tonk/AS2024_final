import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {AttachmentDto, ChatDto, MessageDto} from '../../../gen/dto-chat';
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
import {FileUpload, FileUploadHandlerEvent, FileUploadModule} from 'primeng/fileupload';
import {FileService} from '../../../services/file.service';
import FileSaver from 'file-saver';

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
    FileUploadModule
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

  private reloadInterval: number;

  isNotMyMessage = (message: MessageDto) => message.author?.id !== this.userService.user?.id;

  constructor(private chatService: ChatService,
              private userService: UserService,
              private confirmationService: ConfirmationService,
              private dialogService: DialogService,
              private fileService: FileService) {
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

  async addAttachment(event: FileUploadHandlerEvent, fileUpload: FileUpload) {
    let attachmentDto = await lastValueFrom(this.fileService.uploadFile(event.files[0]));
    this.newMessage.attachments = [...(this.newMessage.attachments ?? []), attachmentDto];
    fileUpload.clear();
  }

  async downloadAttachment(attachment: AttachmentDto) {
    let blob = await lastValueFrom(this.fileService.getAttachment(attachment.id!));
    FileSaver.saveAs(blob, attachment.fileName);
  }
}
