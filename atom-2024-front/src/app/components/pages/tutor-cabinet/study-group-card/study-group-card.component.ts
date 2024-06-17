import {Component, Input, OnInit} from '@angular/core';
import {lastValueFrom} from 'rxjs';
import {NgForOf, NgIf} from '@angular/common';
import {OrderListModule} from 'primeng/orderlist';
import {ListboxModule} from 'primeng/listbox';
import {FormsModule} from '@angular/forms';
import {SplitterModule} from 'primeng/splitter';
import {CoursePanelComponent, CoursePanelMode} from '../../../forms/course-panel/course-panel.component';

@Component({
  selector: 'app-study-group-card',
  standalone: true,
  imports: [
    NgForOf,
    OrderListModule,
    ListboxModule,
    FormsModule,
    SplitterModule,
    NgIf,
    CoursePanelComponent
  ],
  templateUrl: './study-group-card.component.html',
  styleUrl: './study-group-card.component.css'
})
export class StudyGroupCardComponent implements OnInit {
  @Input() studyGroup: any;

  courses: any[] = [];
  students: any[] = [];

  selectedCourse: any;
  selectedStudent: any;

  loading = false;

  constructor() {
  }

  async ngOnInit() {
    await this.init();
  }

  async init() {
    this.loading = true;
    try {
      // TODO Курсы группы для препода, студенты группы
      this.courses = [];
      this.students = [];
    } finally {
      this.loading = false;
    }
  }

  protected readonly CoursePanelMode = CoursePanelMode;
}
