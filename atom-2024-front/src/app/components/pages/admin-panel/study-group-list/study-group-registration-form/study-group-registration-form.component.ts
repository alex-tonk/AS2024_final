import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Button} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {Footer, MessageService} from 'primeng/api';
import {FormsModule} from '@angular/forms';
import {InputTextModule} from 'primeng/inputtext';
import {PaginatorModule} from 'primeng/paginator';
import {StudyGroupDto} from '../../../../../gen/atom2024backend-dto';
import {StudyGroupService} from '../../../../../gen/atom2024backend-controllers';
import {lastValueFrom} from 'rxjs';

@Component({
  selector: 'app-study-group-registration-form',
  standalone: true,
  imports: [
    Button,
    DialogModule,
    Footer,
    FormsModule,
    InputTextModule,
    PaginatorModule
  ],
  templateUrl: './study-group-registration-form.component.html',
  styleUrl: './study-group-registration-form.component.css'
})
export class StudyGroupRegistrationFormComponent implements OnInit {
  visible = true;
  loading = false;
  isEditMode = false;
  studyGroup = new StudyGroupDto();

  @Input() studyGroupId?: number;
  @Output() result = new EventEmitter<StudyGroupDto | null>();


  constructor(private studyGroupService: StudyGroupService,
              private messageService: MessageService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      if (this.studyGroupId) {
        this.studyGroup = await lastValueFrom(this.studyGroupService.getStudyGroup(this.studyGroupId));
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
        this.studyGroup = await lastValueFrom(this.studyGroupService.updateStudyGroup(this.studyGroup.id!, this.studyGroup));
      } else {
        this.studyGroup = await lastValueFrom(this.studyGroupService.createStudyGroup(this.studyGroup));
      }
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Учебная группа сохранена'});
      this.result.emit(this.studyGroup)
    } finally {
      this.loading = false;
    }
  }

  onHide() {
    this.visible = false;
    this.result.emit(null);
  }
}
