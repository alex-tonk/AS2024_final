import {Component, Input} from '@angular/core';
import {CourseDto, StudentDto, StudyGroupDto} from '../../../../gen/atom2024backend-dto';
import {VideoLessonsComponent} from '../video-lessons/video-lessons.component';
import {TimelineModule} from 'primeng/timeline';
import {NgIf} from '@angular/common';
import {BreadcrumbModule} from 'primeng/breadcrumb';

@Component({
  selector: 'app-course-card',
  standalone: true,
  imports: [
    VideoLessonsComponent,
    TimelineModule,
    NgIf,
    BreadcrumbModule
  ],
  templateUrl: './course-card.component.html',
  styleUrl: './course-card.component.css'
})
export class CourseCardComponent {
  @Input() studyGroup: StudyGroupDto;
  @Input() student: StudentDto;
  @Input() course: CourseDto;
  @Input() isTutorMode = false;

  get summary(): string | null {
    if (this.studyGroup && this.student && this.course) {
      return `${this.studyGroup.name!} / ${this.course.name!} / ${this.student.user?.fullName!}`;
    } else {
      return null;
    }
  }
}
