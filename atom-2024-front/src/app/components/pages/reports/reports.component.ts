import {Component, OnInit} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {ButtonModule} from "primeng/button";
import {CheckboxModule} from "primeng/checkbox";
import {ColumnFilterWrapperComponent} from "../../common/table/column-filter-wrapper/column-filter-wrapper.component";
import {InputTextModule} from "primeng/inputtext";
import {MenuModule} from "primeng/menu";
import {MessageService, SharedModule} from "primeng/api";
import {TableModule} from "primeng/table";
import {TooltipModule} from "primeng/tooltip";
import {Column} from "../../common/table/Column";
import {TestGroupService} from "../../../gen/atom2024backend-controllers";
import {lastValueFrom} from "rxjs";
import {ExportTable} from "../../common/table/ExportTable";
import {FormsModule} from "@angular/forms";
import {TestDto} from "../../../gen/atom2024backend-dto";
import {DateFormatter} from "../../common/DateFormatter";
import {SelectButtonModule} from "primeng/selectbutton";
import {LoaderComponent} from "../../common/loader/loader.component";

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    AsyncPipe,
    ButtonModule,
    CheckboxModule,
    ColumnFilterWrapperComponent,
    InputTextModule,
    MenuModule,
    NgForOf,
    NgIf,
    SharedModule,
    TableModule,
    TooltipModule,
    FormsModule,
    SelectButtonModule,
    LoaderComponent
  ],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent implements OnInit {
  tests: TestDto[] = [];
  selectedTest?: TestDto;
  loading = false;
  fetchedData = false;

  filter = false;
  filterByRegDate = 'DAY';
  filtersByRegDate = [
    {label: 'День', value: 'DAY'},
    {label: 'Месяц', value: 'MONTH'},
    {label: 'Квартал', value: 'QUARTER'},
  ]

  columns: Column[] = [{
    header: 'ID',
    field: 'id',
    type: 'numeric',
    width: 10
  }, {
    header: 'Испытание',
    field: 'standEndpointDescription',
  }, {
    header: 'Тип',
    field: 'testType',
  }, {
    header: 'ДСЕ',
    field: 'productCaption',
  }, {
    header: 'Дата регистрации',
    field: 'registrationDate',
    type: 'date'
  }, {
    header: 'Регистратор',
    field: 'registratorShortName',
    width: 25
  }, {
    header: 'Дата начала испытания',
    field: 'executionStartDate',
    type: 'date'
  }, {
    header: 'Дата завершения',
    field: 'executionEndDate',
    type: 'date'
  }, {
    header: 'Испытатель',
    field: 'executorShortName',
    width: 25
  }, {
    header: 'Время выполнения, сек.',
    field: 'executionSeconds',
    type: 'number'
  }]

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  get dateFilterInfo(): string {
    const date = new Date
    const str = 'Данные за ';
    if (this.filterByRegDate === 'MONTH') {
      const dateStart = new Date();
      dateStart.setMonth(dateStart.getMonth() - 1);
      return str + dateStart.toLocaleDateString() + ' - ' + date.toLocaleDateString();
    }
    if (this.filterByRegDate === 'QUARTER') {
      const dateStart = new Date();
      dateStart.setMonth(dateStart.getMonth() - 3);
      return str + dateStart.toLocaleDateString() + ' - ' + date.toLocaleDateString();
    }
    return str + date.toLocaleDateString()
  }

  get getFileName(): string {
    const date = new Date()
    const str = 'Отчет_за_';
    if (this.filterByRegDate === 'MONTH') {
      const dateStart = new Date();
      dateStart.setMonth(dateStart.getMonth() - 1);
      return str + dateStart.toLocaleDateString() + '_' + date.toLocaleDateString();
    }
    if (this.filterByRegDate === 'QUARTER') {
      const dateStart = new Date();
      dateStart.setMonth(dateStart.getMonth() - 3);
      return str + dateStart.toLocaleDateString() + '_' + date.toLocaleDateString();
    }
    return str + date.toLocaleDateString()
  }

  constructor(
    private testGroupService: TestGroupService,
    private messageService: MessageService) {
  }

  async ngOnInit() {
  }

  async getDataFromApi() {
    this.loading = true;
    this.fetchedData = false;
    this.tests = [];

    await new Promise(resolve => setTimeout(resolve, 2000));

    this.selectedTest = undefined;
    try {
      const groups = (await lastValueFrom(this.testGroupService.getTestGroups()));
      const res: TestDto[] = [];
      groups.forEach(g => res.push(...g.tests));

      const period: Date[] = this.getPeriod();
      res.filter(r => {
        return r.registrationDate > period[0] && r.registrationDate < period[1];
      })

      this.tests = res;

      this.messageService.add({
        severity: 'success', summary: 'Выполнено',
        detail: 'Данные сформированы'
      });
    } finally {
      this.loading = false;
      this.fetchedData = true;
    }
  }

  getPeriod(): Date[] {
    if (this.filterByRegDate === 'MONTH') {
      const dateStart = new Date();
      dateStart.setMonth(dateStart.getMonth() - 1);
      return [dateStart, new Date()];
    }
    if (this.filterByRegDate === 'QUARTER') {
      const dateStart = new Date();
      dateStart.setMonth(dateStart.getMonth() - 3);
      return [dateStart, new Date()];
    }
    const dateStart = new Date();
    dateStart.setDate(dateStart.getDate() - 1)
    return [dateStart, new Date()];
  }

  getDate(date: Date) {
    return date == null ? null : DateFormatter.format(DateFormatter.getDateWithZone(date));
  }

  getFieldValue(obj: any, key: string): any {
    const keyParts = key?.split('.') ?? [];
    const firstKeyPart = keyParts.shift();
    let result = (firstKeyPart != null && obj != null) ? obj[firstKeyPart] : null;
    for (const keyPart of keyParts) {
      if (result == null) return null;
      result = result[keyPart];
    }
    return result;
  }

  getTestType(testItem: TestDto): string {
    return testItem.standEndpoint.stand.computationType === 'EMULATED' ? 'Моделирование' : 'Стендовое испытание'
  }

  protected readonly ExportTable = ExportTable;
}
