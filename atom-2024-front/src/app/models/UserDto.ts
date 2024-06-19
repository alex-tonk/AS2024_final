import {RoleDto} from './RoleDto';
import {DateFormatter} from '../components/common/DateFormatter';

export class UserDto {
  id: UserId;
  firstname: string;
  lastname: string;
  email: string;
  password: string;
  registrationDate: Date = new Date();
  roles: RoleDto[];
  surname?: string;
  phoneNumber?: string;
  archived: boolean;
  fullName: string;
  shortName: string;
  rolesAsString: string;
  tutorId?: number;
  studentId?: number;

  get formattedRegistrationDate() {
    return DateFormatter.format(this.registrationDate);
  }

  constructor(source?: any) {
    if (source) {
      Object.assign(this, source);
    }
  }
}

export type UserId = number;
