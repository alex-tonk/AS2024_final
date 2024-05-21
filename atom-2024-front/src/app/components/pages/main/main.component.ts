import {Component} from '@angular/core';
import {CardModule} from "primeng/card";
import {DockModule} from "primeng/dock";
import {ButtonModule} from "primeng/button";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    CardModule,
    DockModule,
    ButtonModule,
    NgForOf
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent {
}
