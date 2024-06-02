import {Component} from '@angular/core';
import {AccordionModule} from "primeng/accordion";
import {NgForOf} from "@angular/common";
import {VideoPlayerComponent} from "../../../common/video-player/video-player.component";
import {NgxDocViewerModule} from "ngx-doc-viewer";

@Component({
  selector: 'app-presentation-lessons',
  standalone: true,
  imports: [
    AccordionModule,
    NgForOf,
    VideoPlayerComponent,
    NgxDocViewerModule
  ],
  templateUrl: './presentation-lessons.component.html',
  styleUrl: './presentation-lessons.component.css'
})
export class PresentationLessonsComponent {
  lessons = [
    {
      title: 'Урок 1',
      presentationSource: '/assets/presentations/test1.pdf',
      description: 'Тестовая pdf презентация'
    },
    {
      title: 'Урок 2',
      presentationSource: 'https://scholar.harvard.edu/files/torman_personal/files/samplepptx.pptx',
      description: 'Тестовая PowerPoint презентация'
    },
    {
      title: 'Урок 3',
      presentationSource: 'https://calibre-ebook.com/downloads/demos/demo.docx',
      description: 'Тестовый Word файл'
    }
  ]

  isOfficeSource(source: string) {
    const sourceToLower = source.toLowerCase();
    return sourceToLower.includes('.pptx') ||
      sourceToLower.includes('.ppt') ||
      sourceToLower.includes('.doc') ||
      sourceToLower.includes('.docx')
  }
}
