import { Component } from '@angular/core';
import {TabViewModule} from "primeng/tabview";
import {UserListComponent} from "../admin-panel/user-list/user-list.component";
import {VideoPlayerComponent} from "../../common/video-player/video-player.component";
import {VideoLessonsComponent} from "./video-lessons/video-lessons.component";
import {PresentationLessonsComponent} from "./presentation-lessons/presentation-lessons.component";

@Component({
  selector: 'app-user-education',
  standalone: true,
  imports: [
    TabViewModule,
    UserListComponent,
    VideoPlayerComponent,
    VideoLessonsComponent,
    PresentationLessonsComponent
  ],
  templateUrl: './user-education.component.html',
  styleUrl: './user-education.component.css'
})
export class UserEducationComponent {

}
