import {UserDto} from '../models/UserDto';

export class AttachmentDto {
  id?: number;
  message?: MessageDto;
  uri?: any;
}

export class ChatDto {
  id?: number;
  members?: UserDto[];
  messages?: MessageDto[];
  name?: string;
}

export class MessageDto {
  attachments?: AttachmentDto[];
  author?: UserDto;
  content?: string;
  createdDate?: Date;
  id?: number;
  readBy?: UserDto[];
}

