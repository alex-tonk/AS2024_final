import {UserDto} from '../models/UserDto';

export class CourseDto {
  id?: number;
  name?: string;
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
  id?: number;
  students?: StudentInGroupDto[];
  tutors?: TutorWithCourseDto[];
}

export class TutorDto {
  id?: number;
  user?: UserDto;
}

export class TutorWithCourseDto {
  course?: CourseDto;
  studyGroup?: StudyGroupDto;
  tutor?: TutorDto;
}

