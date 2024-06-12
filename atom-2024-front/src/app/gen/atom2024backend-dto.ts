import {UserDto} from '../models/UserDto';

export class CourseDto {
  id?: number;
  modules?: ModuleDto[];
  name?: string;
}

export class CourseWithTutorsDto {
  course?: CourseDto;
  studyGroup?: StudyGroupDto;
  tutorNames?: string;
  tutors?: TutorDto[];
  tutorsCount?: number;
}

export class ModuleDto {
  id?: number;
  module?: ModuleDto;
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
  courseNames?: string;
  courses?: CourseWithTutorsDto[];
  coursesCount?: number;
  id?: number;
  name?: string;
  studentNames?: string;
  students?: StudentInGroupDto[];
  studentsCount?: number;
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

