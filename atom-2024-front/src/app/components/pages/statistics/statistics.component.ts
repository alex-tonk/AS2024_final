import {Component} from '@angular/core';
import {TabViewModule} from 'primeng/tabview';
import {TasksStatisticsComponent} from './tasks-statistics/tasks-statistics.component';
import {StudentRankingComponent} from './student-ranking/student-ranking.component';

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [
    TabViewModule,
    TasksStatisticsComponent,
    StudentRankingComponent
  ],
  templateUrl: './statistics.component.html',
  styleUrl: './statistics.component.css'
})
export class StatisticsComponent {

}
