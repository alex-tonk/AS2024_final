import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-online-lessons',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './online-lessons.component.html',
  styleUrl: './online-lessons.component.css'
})
export class OnlineLessonsComponent {
  @Input() isActive = false;
}
