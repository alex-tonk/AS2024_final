import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {DialogModule} from 'primeng/dialog';
import {LessonDto, SupplementDto} from '../../../../gen/atom2024backend-dto';
import {MarkdownComponent, provideMarkdown} from 'ngx-markdown';
import {ConfigService} from '../../../../services/config.service';
import {InputSwitchModule} from 'primeng/inputswitch';
import {FormsModule} from '@angular/forms';
import {StepperModule} from 'primeng/stepper';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {debounceTime, distinctUntilChanged, fromEvent, lastValueFrom, Observable, of} from 'rxjs';
import {FileService} from '../../../../services/file.service';
import FileSaver from 'file-saver';
import {NgxMarkjsModule} from 'ngx-markjs';
import {map} from 'rxjs/operators';
import {Button} from 'primeng/button';
import {TooltipModule} from 'primeng/tooltip';

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
    NgForOf,
    TooltipModule,
    NgForOf,
    NgxMarkjsModule,
    AsyncPipe,
    Button
  ],
  templateUrl: './lecture-view.component.html',
  styleUrl: './lecture-view.component.css',
  providers: [provideMarkdown()]
})
export class LectureViewComponent implements OnInit, AfterViewInit {
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

  @ViewChild('search', {static: false}) searchElemRef: ElementRef | undefined;
  searchText$: Observable<string | null> = of(null);
  searchConfig = {separateWordSearch: false};

  searchResults: HTMLCollectionOf<HTMLElement>;
  currentResult = 0;

  constructor(private configService: ConfigService,
              private fileService: FileService) {
  }

  ngOnInit() {
    this.content = (this.lesson?.content ?? '').replaceAll('<baseUrl>', this.configService.baseUrl);
    this.beautifiedContent = this.content.replaceAll('<br>', '\n');
  }

  ngAfterViewInit() {
    this.searchText$ = fromEvent<Event>(this.searchElemRef?.nativeElement, 'keyup').pipe(
      map((e: Event) => {
        setTimeout(() => {
          this.searchResults = document.getElementsByTagName('mark');
          this.currentResult = -1;
          this.scrollToPrevResult();
        }, 350);
        return (e.target as HTMLInputElement).value;
      }),
      debounceTime(300),
      distinctUntilChanged()
    );
  }

  async downloadLessonFile(s: SupplementDto) {
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

  scrollToPrevResult() {
    this.searchResults.item(this.currentResult)?.classList.remove('current-mark');
    this.currentResult = (this.currentResult + 1) % this.searchResults.length;
    this.searchResults.item(this.currentResult)?.classList.add('current-mark');
    this.searchResults.item(this.currentResult)?.scrollIntoView({behavior: 'smooth'});
  }

  scrollToNextResult() {
    this.searchResults.item(this.currentResult)?.classList.remove('current-mark');
    this.currentResult = this.currentResult - 1;
    if (this.currentResult < 0) this.currentResult += this.searchResults.length;
    this.searchResults.item(this.currentResult)?.classList.add('current-mark');
    this.searchResults.item(this.currentResult)?.scrollIntoView({behavior: 'smooth'});
  }
}
