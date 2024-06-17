import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {VideoLessonsComponent} from '../../pages/student-cabinet/samples/video-lessons/video-lessons.component';
import {AccordionModule} from 'primeng/accordion';
import {NgForOf, NgIf} from '@angular/common';
import {VideoPlayerComponent} from '../../common/video-player/video-player.component';
import {Button} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {TooltipModule} from 'primeng/tooltip';
import {FormsModule} from '@angular/forms';
import {PanelModule} from 'primeng/panel';


export enum CoursePanelMode {
  STUDENT = 'STUDENT', TUTOR = 'TUTOR', ADMIN = 'ADMIN'
}

@Component({
  selector: 'app-course-panel',
  standalone: true,
  imports: [
    VideoLessonsComponent,
    AccordionModule,
    NgForOf,
    VideoPlayerComponent,
    Button,
    InputTextModule,
    NgIf,
    TooltipModule,
    FormsModule,
    PanelModule
  ],
  templateUrl: './course-panel.component.html',
  styleUrl: './course-panel.component.css'
})
export class CoursePanelComponent implements OnInit {
  @Input() mode: CoursePanelMode = CoursePanelMode.STUDENT;

  @Input() studyGroup: any;
  @Input() student: any;
  @Input() course: any;

  @Output() backToCourseList = new EventEmitter();

  courseModules: any[] = [
    {
      name: 'Бизнес-анализ',
      lessons: ['Урок 1', 'Урок 2', 'Промежуточный тест', 'Воркшоп', 'Контрольный тест']
    },
    {
      name: 'UI / UX',
      lessons: ['Адаптивность', 'User-friendly', 'Домашнее задание']
    },
    {
      name: 'Фронтенд разработка',
      lessons: ['HTML', 'CSS', 'TYPESCRIPT', 'Контрольный тест']
    },
    {
      name: 'Бэкэнд',
      lessons: ['JAVA', 'SPRING BOOT', 'Контрольный тест']
    }
  ];

  loading = false;
  filterValue: string;

  get filteredModules() {
    if (this.filterValue) {
      return this.courseModules.filter(g => JSON.stringify(g).toLowerCase().includes(this.filterValue.toLowerCase()));
    } else {
      return this.courseModules;
    }
  }

  get header(): string {
    switch (this.mode) {
      case CoursePanelMode.STUDENT: return `Курс: ${this.course.name!}`
      case CoursePanelMode.ADMIN: return `Конфигуратор курса: ${this.course.name!}`;
      case CoursePanelMode.TUTOR: return `${this.studyGroup.name!} / ${this.course.name!} / ${this.student.user?.fullName!}`
    }
  }

  constructor() {
  }

  ngOnInit(): void {
  }

  createModule() {
    alert('Создание нового модуля')
  }

  editModule(module: any, event: MouseEvent) {
    // Для предотвращения открытия аккардиона
    event.stopImmediatePropagation();
    alert('Редактирование модуля ' + module.name)
  }

  deleteModule(module: any, event: MouseEvent) {
    event.stopImmediatePropagation();
    alert('Удаление модуля ' + module.name)
  }

  protected readonly CoursePanelMode = CoursePanelMode;
}
