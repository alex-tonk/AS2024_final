import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DialogModule} from 'primeng/dialog';
import {LessonDto, SupplementDto} from '../../../../gen/atom2024backend-dto';
import {MarkdownComponent, provideMarkdown} from 'ngx-markdown';
import {ConfigService} from '../../../../services/config.service';
import {InputSwitchModule} from 'primeng/inputswitch';
import {FormsModule} from '@angular/forms';
import {StepperModule} from 'primeng/stepper';
import {NgForOf, NgIf} from '@angular/common';
import {lastValueFrom} from 'rxjs';
import {FileService} from '../../../../services/file.service';
import FileSaver from 'file-saver';

@Component({
  selector: 'app-lecture-view',
  standalone: true,
  imports: [
    DialogModule,
    MarkdownComponent,
    InputSwitchModule,
    FormsModule,
    StepperModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './lecture-view.component.html',
  styleUrl: './lecture-view.component.css',
  providers: [provideMarkdown()]
})
export class LectureViewComponent implements OnInit {
  @Input()
  lesson: LessonDto;

  @Output()
  lectureViewEnd: EventEmitter<void> = new EventEmitter<void>();

  visible = true;
  loading = false;
  original = false;
  content: string;
  beautifiedContent: string;
  stepperIndex = 0;

  constructor(private configService: ConfigService,
              private fileService: FileService) {
  }

  ngOnInit() {
    this.content = (this.lesson?.content ?? '').replaceAll('<baseUrl>', this.configService.baseUrl);
    this.beautifiedContent = this.content.replaceAll('<br>', '\n');
  }

  async downloadLessonFile(s: SupplementDto) {
    if (s?.fileId == null) {
      return;
    }

    this.loading = true;
    try {
      let blob = await lastValueFrom(this.fileService.getLessonFile(s.fileId));
      FileSaver.saveAs(blob, s.title);
    } finally {
      this.loading = false;
    }
  }
}
