import {Component} from '@angular/core';
import {TooltipModule} from "primeng/tooltip";

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [
    TooltipModule
  ],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.css'
})
export class LoaderComponent {

}
