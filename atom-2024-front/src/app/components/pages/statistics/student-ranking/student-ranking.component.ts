import {Component, OnInit} from '@angular/core';
import {Button} from "primeng/button";
import {CheckboxModule} from "primeng/checkbox";
import {
    ColumnFilterWrapperComponent
} from "../../../common/table/column-filter-wrapper/column-filter-wrapper.component";
import {InputTextModule} from "primeng/inputtext";
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {PrimeTemplate} from "primeng/api";
import {TableModule} from "primeng/table";
import {TagModule} from "primeng/tag";
import {TooltipModule} from "primeng/tooltip";
import {getField} from '../../../../services/field-accessor';
import {FormsModule} from '@angular/forms';
import {Column} from '../../../common/table/Column';
import {ExportTable} from '../../../common/table/ExportTable';
import {MenuModule} from 'primeng/menu';
import {StudentRankingDto} from '../../../../gen/atom2024backend-dto';
import {lastValueFrom} from 'rxjs';
import {StatisticsService} from '../../../../gen/atom2024backend-controllers';
import {DropdownModule} from "primeng/dropdown";

@Component({
  selector: 'app-student-ranking',
  standalone: true,
  imports: [
    Button,
    CheckboxModule,
    ColumnFilterWrapperComponent,
    InputTextModule,
    NgForOf,
    NgIf,
    PrimeTemplate,
    TableModule,
    TagModule,
    TooltipModule,
    FormsModule,
    AsyncPipe,
    MenuModule,
    DropdownModule
  ],
  templateUrl: './student-ranking.component.html',
  styleUrl: './student-ranking.component.css'
})
export class StudentRankingComponent implements OnInit {

  protected readonly getField = getField;
  columns: Column[] = [
    {
      header: 'Место в рейтинге',
      field: 'rank',
      type: 'rank',
      width: 10
    },
    {
      header: 'Обучающийся',
      field: 'fullName',
      width: 25
    },
    {
      header: 'Суммарный балл',
      field: 'totalMark',
      type: 'numeric'
    },
    {
      header: 'Средний балл',
      field: 'mark',
      type: 'numeric'
    },
    {
      header: 'Суммарное время выполнения',
      field: 'totalCompleteTimeSecondsLocale',
    },
    {
      header: 'Среднее время выполнения',
      field: 'completeTimeSecondsLocale'
    },
    {
      header: 'Число вып. заданий',
      field: 'completeTaskCount',
      type: 'numeric'
    }
  ];

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  protected readonly ExportTable = ExportTable;
  filter = false;
  loading = false;
  rankings: StudentRankingDto[] = [];
  selectedRanking?: StudentRankingDto;
  rankingOptions = [
    {label: 'суммарным значениям', value: true},
    {label: 'средним значениям', value: false}
  ]
  onSum = true;

  constructor(private statisticsService: StatisticsService) {
  }

  async ngOnInit() {
       await this.getRankingsFromApi();
  }

  async getRankingsFromApi() {
    this.loading = true;
    try {
      this.rankings = await lastValueFrom(this.statisticsService.getStudentRankings(this.onSum, null));
    } finally {
      this.loading = false;
    }
  }

  getCrownStyle(rank: number) {
    const style: any = {paddingLeft: '1rem'};
    if (rank === 1) {
      style.color = 'gold';
    } else if (rank === 2) {
      style.color = 'silver';
    } else if (rank === 3) {
      style.color = 'bronze';
    }
    return style;
  }
}
