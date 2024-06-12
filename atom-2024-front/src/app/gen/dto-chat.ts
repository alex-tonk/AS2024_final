import {UserDto} from '../models/UserDto';
import {ChatType} from './entities-enums';

export class AttachmentDto {
  fileId?: number;
  fileName?: string;
  id?: number;
  message?: MessageDto;
}

export class ChatDto {
  id?: number;
  lastMessage?: MessageDto;
  members?: UserDto[];
  messages?: MessageDto[];
  name?: string;
  type?: ChatType;
}

export class MessageDto {
  attachments?: AttachmentDto[];
  author?: UserDto;
  content?: string;
  createdDate?: Date;
  id?: number;
  readBy?: UserDto[];
}

