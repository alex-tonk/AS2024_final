import {Component} from '@angular/core';
import {TabViewModule} from 'primeng/tabview';
import {UserListComponent} from '../admin-panel/user-list/user-list.component';
import {VideoPlayerComponent} from '../../common/video-player/video-player.component';
import {VideoLessonsComponent} from './video-lessons/video-lessons.component';
import {PresentationLessonsComponent} from './presentation-lessons/presentation-lessons.component';
import {OnlineLessonsComponent} from './online-lessons/online-lessons.component';

@Component({
  selector: 'app-student-cabinet',
  standalone: true,
  imports: [
    TabViewModule,
    UserListComponent,
    VideoPlayerComponent,
    VideoLessonsComponent,
    PresentationLessonsComponent,
    OnlineLessonsComponent
  ],
  templateUrl: './student-cabinet.component.html',
  styleUrl: './student-cabinet.component.css'
})
export class StudentCabinetComponent {

}
