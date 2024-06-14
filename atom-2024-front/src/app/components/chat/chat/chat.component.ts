import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {AttachmentDto, ChatDto, MessageDto} from '../../../gen/dto-chat';
import {ChatService, UserAdminService} from '../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';
import {UserService} from '../../../services/user.service';
import {FormsModule} from '@angular/forms';
import {ChipsModule} from 'primeng/chips';
import {ButtonModule} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {ChatRegistrationDialogComponent} from '../chat-registration-dialog/chat-registration-dialog.component';
import {ConfirmationService, MessageService} from 'primeng/api';
import {TooltipModule} from 'primeng/tooltip';
import {DialogService} from 'primeng/dynamicdialog';
import {Listbox, ListboxModule} from 'primeng/listbox';
import {FileUpload, FileUploadHandlerEvent, FileUploadModule} from 'primeng/fileupload';
import {FileService} from '../../../services/file.service';
import FileSaver from 'file-saver';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {BadgeModule} from 'primeng/badge';
import {SplitterModule} from 'primeng/splitter';
import {SkeletonModule} from 'primeng/skeleton';
import {ChatType} from '../../../gen/entities-enums';
import {DropdownModule} from 'primeng/dropdown';
import {UserDto} from '../../../models/UserDto';
import {InputTextareaModule} from 'primeng/inputtextarea';

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
    FileUploadModule,
    OverlayPanelModule,
    BadgeModule,
    SplitterModule,
    SkeletonModule,
    DropdownModule,
    ListboxModule,
    InputTextareaModule
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
  selectedChat?: ChatDto;
  newMessage: MessageDto = new MessageDto();

  loading = false;
  isChatMemberListVisible = false;

  addingUser?: UserDto;
  allUsers: UserDto[] = [];

  private reloadInterval: number;

  isNotMyMessage = (message: MessageDto) => message.author?.id !== this.userService.user?.id;

  filterValue: string;

  get filteredChats() {
    if (this.filterValue) {
      return this.chats.filter(c => JSON.stringify(c).toLowerCase().includes(this.filterValue.toLowerCase()))
    } else {
      return this.chats;
    }
  }

  get filteredUsers() {
    if (this.selectedChat && this.selectedChat.members) {
      const currentIds = this.selectedChat.members.map(m => m.id);
      return this.allUsers.filter(u => !currentIds.includes(u.id));
    } else {
      return this.allUsers;
    }
  }

  constructor(private chatService: ChatService,
              private userService: UserService,
              private userAdminService: UserAdminService,
              private confirmationService: ConfirmationService,
              private messageService: MessageService,
              private dialogService: DialogService,
              private fileService: FileService) {
  }

  async onKeyPress(event: KeyboardEvent) {
    if (event.key?.toLowerCase() !== 'enter' || !this.newMessage.content || this.selectedChat?.id == null) {
      return;
    }
    if (event.shiftKey) {
      return;
    }
    await this.sendMessage();
  }

  async reloadChat() {
    const newChats = await lastValueFrom(this.chatService.getChats());
    if (!this.chatListsEquals(this.chats, newChats)) {
      this.chats = newChats;
    }
    if (this.selectedChat?.id == null) {
      return;
    }
    try {
      let newChat = await lastValueFrom(this.chatService.getChat(this.selectedChat.id));
      if (!this.chatsEquals(this.selectedChat, newChat)) {
        this.selectedChat = newChat;
      }
    } catch (e) {
      this.selectedChat = undefined;
      throw e;
    }
  }

  async ngOnInit(): Promise<void> {
    this.chats = await lastValueFrom(this.chatService.getChats());
    this.allUsers = await lastValueFrom(this.userAdminService.getUsers());

    this.reloadInterval = setInterval(this.reloadChat.bind(this), 1000);
  }

  ngOnDestroy() {
    clearInterval(this.reloadInterval);
  }

  async sendMessage() {
    if (this.selectedChat?.id == null) {
      return;
    }

    await lastValueFrom(this.chatService.addMessage(this.selectedChat.id, this.newMessage));
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
      const result = await lastValueFrom(this.chatService.createChat(event));
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Чат создан'});
      await this.reloadChat();
      await this.selectChat(result);
      this.registrationDialogVisible = false;
    } finally {
      this.registrationDialogLoading = false;
    }
  }

  async selectChat(chat: ChatDto) {
    this.selectedChat = await lastValueFrom(this.chatService.getChat(chat.id!));
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
          if (this.selectedChat?.id === chat.id) {
            this.selectedChat = undefined;
          }
        } finally {
          this.chats = await lastValueFrom(this.chatService.getChats());
        }
      }
    });
  }

  showMembers() {
    this.addingUser = undefined;
    this.isChatMemberListVisible = true;
  }

  async addUserToChat() {
    this.loading = true;
    try {
      if (this.selectedChat && this.addingUser) {
        await lastValueFrom(this.chatService.addUserToChat(this.selectedChat?.id!, this.addingUser?.id!));
        this.messageService.add({severity: 'success', summary: 'Выполнено', detail: `${this.addingUser.fullName} добавлен в чат`});
        await this.reloadChat();
        this.addingUser = undefined;
      }
    } finally {
      this.loading = false;
    }
  }

  async addAttachment(event: FileUploadHandlerEvent, fileUpload: FileUpload) {
    let attachmentDto = await lastValueFrom(this.fileService.uploadFile(event.files[0]));
    this.newMessage.attachments = [...(this.newMessage.attachments ?? []), attachmentDto];
    fileUpload.clear();
  }

  async downloadAttachment(attachment: AttachmentDto) {
    let blob = await lastValueFrom(this.fileService.getAttachment(attachment.fileId!));
    FileSaver.saveAs(blob, attachment.fileName);
  }

  private chatListsEquals(chatsA: ChatDto[], chatsB: ChatDto[]): boolean {
    if (chatsA === chatsB) return true;
    if (chatsA.length !== chatsB.length) return false;

    return chatsA.every((c, i) => c.id === chatsB[i].id && c.lastMessage?.id === chatsB[i]?.lastMessage?.id);
  }

  private chatsEquals(chatA: ChatDto, chatB: ChatDto): boolean {
    if (chatA === chatB) return true;
    if (chatA.messages?.length !== chatB.messages?.length) return false;
    if (chatA.members?.length !== chatB.members?.length) return false;

    let messagesEquals = !!chatA.messages && !!chatB.messages || !chatA.messages || !chatB.messages
      || chatA.messages!.every((m, i) => m.id === chatB.messages![i].id);
    let membersEquals = !!chatA.members && !!chatB.members || !chatA.members || !chatB.members
      || chatB.members!.every((m, i) => m.id === chatB.members![i].id);

    return messagesEquals && membersEquals;
  }

  async removeAttachment(attachment: AttachmentDto) {
    this.newMessage.attachments = this.newMessage.attachments?.filter(v => v !== attachment);
    await lastValueFrom(this.fileService.deleteFile(attachment.fileId!));
  }

  protected readonly ChatType = ChatType;
}
