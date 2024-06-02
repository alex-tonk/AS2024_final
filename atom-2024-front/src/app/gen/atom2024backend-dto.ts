import {UserDto} from '../models/UserDto';

export class CourseDto {
  id?: number;
  name?: string;
}

export class CourseWithTutorsDto {
  course?: CourseDto;
  studyGroup?: StudyGroupDto;
  tutors?: TutorDto[];
}

export class StudentDto {
  id?: number;
  user?: UserDto;
}

export class StudentInGroupDto {
  student?: StudentDto;
  studyGroup?: StudyGroupDto;
}

export class StudyGroupDto {
  courses?: CourseWithTutorsDto[];
  id?: number;
  name?: string;
  students?: StudentInGroupDto[];
}

export class TutorDto {
  id?: number;
  user?: UserDto;
}

export class TutorInCourseDto {
  course?: CourseDto;
  studyGroup?: StudyGroupDto;
  tutor?: TutorDto;
}

