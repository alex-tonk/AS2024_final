import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Button} from "primeng/button";
import {DialogModule} from "primeng/dialog";
import {DropdownModule} from "primeng/dropdown";
import {Footer, MessageService} from "primeng/api";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {NgIf} from "@angular/common";
import {CourseService} from "../../../../../gen/atom2024backend-controllers";
import {lastValueFrom} from "rxjs";
import {CourseDto} from "../../../../../gen/atom2024backend-dto";

@Component({
  selector: 'app-course-registration-form',
  standalone: true,
  imports: [
    Button,
    DialogModule,
    DropdownModule,
    Footer,
    FormsModule,
    InputTextModule,
    NgIf
  ],
  templateUrl: './course-registration-form.component.html',
  styleUrl: './course-registration-form.component.css'
})
export class CourseRegistrationFormComponent implements OnInit {
  visible = true;
  loading = false;
  isEditMode = false;
  course = new CourseDto();

  @Input() courseId?: number;
  @Output() result = new EventEmitter<CourseDto | null>();


  constructor(private courseService: CourseService,
              private messageService: MessageService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      if (this.courseId) {
        this.course = await lastValueFrom(this.courseService.getCourse(this.courseId));
        this.isEditMode = true;
      }
    } finally {
      this.loading = false;
    }
  }

  async save() {
    this.loading = true;
    try {
      if (this.isEditMode) {
        this.course = await lastValueFrom(this.courseService.updateCourse(this.course.id!, this.course));
      } else {
        this.course = await lastValueFrom(this.courseService.createCourse(this.course));
      }
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Курс сохранен'});
      this.result.emit(this.course)
    } finally {
      this.loading = false;
    }
  }

  onHide() {
    this.visible = false;
    this.result.emit(null);
  }
}
