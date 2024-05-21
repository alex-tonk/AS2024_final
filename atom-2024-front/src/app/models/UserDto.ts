import {RoleDto} from "./RoleDto";
import {DateFormatter} from "../components/common/DateFormatter";
import {StandEndpointDto} from "../gen/atom2024backend-dto";

export class UserDto {
  id: UserId;
  firstname: string;
  lastname: string;
  email: string;
  password: string;
  registrationDate: Date = new Date();
  roles: RoleDto[];
  // TODO
  availableStandEndpoints: StandEndpointDto[];
  surname?: string;
  phoneNumber?: string;
  archived: boolean;
  fullName: string;
  rolesAsString: string;
  // TODO
  testTypesAsString: string;

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
