import {Component, Input} from '@angular/core';
import {AccordionModule} from "primeng/accordion";
import {NgForOf, NgIf} from "@angular/common";
import {VideoPlayerComponent} from "../../../common/video-player/video-player.component";
import _default from "chart.js/dist/core/core.interaction";


@Component({
  selector: 'app-video-lessons',
  standalone: true,
  imports: [
    AccordionModule,
    NgForOf,
    VideoPlayerComponent,
    NgIf
  ],
  templateUrl: './video-lessons.component.html',
  styleUrl: './video-lessons.component.css'
})
export class VideoLessonsComponent {
  activeIndex: number;

  lessons = [
    {
      title: 'Урок 1',
      videoSource: '/assets/videos/lesson1.mp4',
      description: 'Урок на тему хорошего аниме. Мне кажется, невероятная популярность «Атаки» кроется в грамотном и постепенном знакомстве с этой вселенной. Ты осознаешь масштаб детальной проработки вплоть до физическо-научного подхода к технологиям, при этом информацию о мифологии и истории получаешь маленькими порциями. Ощущение, что все продумано наперед, не покидало меня с первых серий, поэтому интрига и уворачивание от спойлеров из манги создавали свой многолетний развлекательный опыт. Но меня, помимо прочего, очень зацепила линия молодых ребят на войне — вообще не припомню такого погружения в эту тему. В «Атаке» много загадок, она эпична, у сериала потрясающая анимация, динамичнейший вход в историю и максимально грамотные акценты на клиффхэнгерах. В каких сериалах на середине сезона вам показывают смерть главного героя! «Игре престолов» такое не снилось. Потом — бац! — новый поворот. Бац — еще один! И вот ты уже на игле из загадок и интриг, и так все сезоны.'
    },
    {title: 'Урок 2', videoSource: '/assets/videos/lesson2.mp4', description: 'Когда ничего не понял в первом уроке.'},
    {title: 'Домашнее задание', videoSource: '/assets/videos/lesson2.mp4', description: 'Обоснуй видос', type: 'homework'}
  ]

  private _isActive = false;
  @Input() get isActive(): boolean {
    return this._isActive;
  }

  set isActive(value: boolean) {
    this._isActive = value;
    if (!value) {
      this.activeIndex = -1;
    }
  }

  setActiveIndex(index: number) {
    this.activeIndex = index;
  }
}
