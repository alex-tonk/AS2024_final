import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MonacoEditorModule} from "ngx-monaco-editor-v2";
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from "primeng/button";
import {DropdownModule} from "primeng/dropdown";
import {NgForOf, NgIf} from "@angular/common";
import {TooltipModule} from "primeng/tooltip";
import {CalendarModule} from "primeng/calendar";
import {InputNumberModule} from "primeng/inputnumber";
import {SelectButtonModule} from "primeng/selectbutton";
import {CheckboxModule} from "primeng/checkbox";

@Component({
  selector: 'app-code-editor',
  standalone: true,
  imports: [
    FormsModule,
    MonacoEditorModule,
    InputTextModule,
    ButtonModule,
    DropdownModule,
    NgForOf,
    TooltipModule,
    NgIf,
    CalendarModule,
    InputNumberModule,
    SelectButtonModule,
    CheckboxModule,
  ],
  templateUrl: './code-editor.component.html',
  styleUrl: './code-editor.component.css',
})
export class CodeEditorComponent implements OnInit {
  loading = false;

  @Input() code: string;
  @Output() codeChange = new EventEmitter<string>();

  options = {theme: 'vs', language: 'javascript'};

  constructor() {
  }

  ngOnInit(): void {
    setTimeout(() => {
      if (!this.code || this.code.length === 0) {
        this.code = '/*\n' +
          'Напишите код на языке javascript\n' +
          'Входные параметры, указанные в настройке доступны через объект params \n' +
          'Информация о ДСЕ доступна через объект product \n' +
          'Код должен содержать возврат выходного объекта, содержащего поля, \n' +
          'указанные в настройке\n' +
          'function sum(a, b) {\n' +
          ' return a+b;\n' +
          '}\n' +
          'return { VARIABLE_1: sum(params.VARIABLE_1, params.VARIABLE_2), VARIABLE_2: params.VARIABLE_3 }\n' +
          '*/\n';
        this.onCodeChange();
      }
    })
  }

  onCodeChange() {
    this.codeChange.emit(this.code);
  }
}
