import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DialogModule} from 'primeng/dialog';
import {InputSwitchModule} from 'primeng/inputswitch';
import {MarkdownComponent, provideMarkdown} from 'ngx-markdown';
import {NgForOf, NgIf} from '@angular/common';
import {PrimeTemplate} from 'primeng/api';
import {StepperModule} from 'primeng/stepper';
import {AttemptDto, LessonDto, SupplementDto, TaskDto, TopicDto} from '../../../../gen/atom2024backend-dto';
import {ConfigService} from '../../../../services/config.service';
import {FileService} from '../../../../services/file.service';
import {lastValueFrom} from 'rxjs';
import FileSaver from 'file-saver';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {AttemptService} from '../../../../gen/atom2024backend-controllers';
import {AttemptStatus} from '../../../../gen/atom2024backend-enums';
import {PanelModule} from 'primeng/panel';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {FileUpload, FileUploadHandlerEvent, FileUploadModule} from 'primeng/fileupload';

@Component({
  selector: 'app-task-attempt',
  standalone: true,
  imports: [
    DialogModule,
    InputSwitchModule,
    MarkdownComponent,
    NgForOf,
    NgIf,
    PrimeTemplate,
    StepperModule,
    FormsModule,
    Button,
    PanelModule,
    InputTextareaModule,
    FileUploadModule
  ],
  templateUrl: './task-attempt.component.html',
  styleUrl: './task-attempt.component.css',
  providers: [provideMarkdown()]
})
export class TaskAttemptComponent {
  @Input()
  formData: { topic: TopicDto, lesson: LessonDto, task: TaskDto };

  @Input()
  taskAttempt: AttemptDto;

  @Output()
  taskAttemptClose: EventEmitter<void> = new EventEmitter<void>();

  get task() {
    return this.formData.task;
  }

  get lesson() {
    return this.formData.lesson;
  }

  get topic() {
    return this.formData.topic;
  }

  visible = true;
  loading = false;
  original = false;
  content: string;
  beautifiedContent: string;
  stepperIndex = 0;
  stepsCount = 3;

  constructor(private configService: ConfigService,
              private fileService: FileService,
              private attemptService: AttemptService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      this.content = (this.task?.content ?? '').replaceAll('<baseUrl>', this.configService.baseUrl);
      this.beautifiedContent = this.content.replaceAll('<br>', '\n');
      const lastAttempt = await lastValueFrom(this.attemptService.getLastAttempt(this.topic.id!, this.lesson.id!, this.task.id!));
      // TODO: viewAttempt after check
      if (lastAttempt != null && lastAttempt.status === AttemptStatus.IN_PROGRESS) {
        this.taskAttempt = lastAttempt;
        this.stepperIndex = 2;
      }
    } finally {
      this.loading = false;
    }
  }

  async downloadTaskFile(s: SupplementDto) {
    if (s?.fileId == null) {
      return;
    }

    this.loading = true;
    try {
      let blob = await lastValueFrom(this.fileService.getLessonFile(s.fileId));
      FileSaver.saveAs(blob, s.title || s.fileName);
    } finally {
      this.loading = false;
    }
  }

  getTimeLimitString() {
    if (this.task.time == null) {
      return '';
    }
    if (this.task.time >= 60) {
      const timeLimitHours = Math.round(this.task.time / 60);
      let hoursLabel = 'час';
      if ([11, 12, 13, 14].includes(timeLimitHours % 100)) {
        hoursLabel = 'часов';
      } else {
        switch (timeLimitHours % 10) {
          case 1:
            hoursLabel = 'час';
            break;
          case 2:
          case 3:
          case 4:
            hoursLabel = 'часа';
            break;
          default:
            hoursLabel = 'часов';
        }
        hoursLabel = 'час';
      }
      if ((this.task.time % 60) == 0) {
        return `${timeLimitHours} ${hoursLabel}`;
      }
      return `${timeLimitHours} ${hoursLabel} ${this.task.time % 60} мин.`;
    } else {
      return `${this.task.time} мин.`
    }
  }

  async startTaskAttempt() {
    this.loading = true;
    try {
      let attempt = await lastValueFrom(this.attemptService.startNewAttempt(this.topic.id!, this.lesson.id!, this.task.id!));
      this.taskAttempt = attempt;
    } finally {
      this.loading = false;
    }
  }

  nextStep() {
    this.stepperIndex += 1;
  }

  previousStep() {
    this.stepperIndex -= 1;
  }

  sendToCheck() {

  }

  async addAttemptFile(event: FileUploadHandlerEvent, fileUpload: FileUpload) {
    this.loading = true;
    try {
      const fileName = event.files[0].name;
      const fileId = await lastValueFrom(this.fileService.uploadAttemptFile(event.files[0]));
      this.taskAttempt.files = [...(this.taskAttempt.files ?? []), {fileId: fileId, fileName: fileName}];
    } finally {
      setTimeout(() => fileUpload.clear());
      this.loading = false;
    }
  }
}
