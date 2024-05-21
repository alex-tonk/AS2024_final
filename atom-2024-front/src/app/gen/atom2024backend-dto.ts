import {UserDto} from '../models/UserDto';
import {Metadata} from './common-parsing';
import {ComputationType, TestStatus} from './entities-enums';

export interface ProductDto {
  caption: string;
  code: string;
  globalId: number;
  id: number;
  standId: number;
}

export interface StandDto {
  computationType: ComputationType;
  description: string;
  id: number;
  name: string;
  url: string;
}

export interface StandEndpointDto {
  description: string;
  id: number;
  jsCode: string;
  name: string;
  stand: StandDto;
  standEndpointType: StandEndpointTypeDto;
  url: string;
}

export interface StandEndpointTypeDto {
  id: number;
  inMeta: Metadata;
  name: string;
  outMeta: Metadata;
}

export interface TestDto {
  executionEndDate: Date;
  executionSeconds: number;
  executionStartDate: Date;
  executorShortName: string;
  id: number;
  inData: Object;
  outData: Object;
  outerId: number;
  product: ProductDto;
  productCaption: string;
  registrationDate: Date;
  standEndpoint: StandEndpointDto;
  standEndpointDescription: string;
  testStatus: TestStatus;
  testType: string;
}

export interface TestGroupDto {
  comment: string;
  endDate: Date;
  executionSeconds: number;
  hasError: boolean;
  id: number;
  isBusy: boolean;
  isStopped: boolean;
  startDate: Date;
  startUser: UserDto;
  tests: TestDto[];
}

