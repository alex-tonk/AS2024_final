import {Component, Input} from '@angular/core';
import {CalendarModule} from "primeng/calendar";
import {InputNumberModule} from "primeng/inputnumber";
import {InputTextModule} from "primeng/inputtext";
import {SharedModule} from "primeng/api";
import {TableModule} from "primeng/table";
import {Column} from "../Column";
import {TriStateCheckboxModule} from "primeng/tristatecheckbox";
import {SelectButtonModule} from "primeng/selectbutton";
import {FormsModule} from "@angular/forms";
import {DateFormatter} from "../../DateFormatter";

@Component({
  selector: 'app-column-filter-wrapper',
  standalone: true,
  imports: [
    CalendarModule,
    InputNumberModule,
    InputTextModule,
    SharedModule,
    TableModule,
    TriStateCheckboxModule,
    SelectButtonModule,
    FormsModule
  ],
  templateUrl: './column-filter-wrapper.component.html',
  styleUrl: './column-filter-wrapper.component.css'
})
export class ColumnFilterWrapperComponent {
  @Input() column: Column;
  booleanValues = [
    {value: true, label: 'Да'},
    {value: false, label: 'Нет'}
  ];

  convertDateToUtc(date: Date | null): Date | null {
    return date == null ? null : DateFormatter.getDateWithZone(date);
  }
}
