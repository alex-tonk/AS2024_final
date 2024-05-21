import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MetadataNodeType} from "../../../../gen/parsing-enums";
import {ButtonModule} from "primeng/button";
import {CalendarModule} from "primeng/calendar";
import {CheckboxModule} from "primeng/checkbox";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputNumberModule} from "primeng/inputnumber";
import {InputTextModule} from "primeng/inputtext";
import {NgForOf, NgIf} from "@angular/common";
import {TooltipModule} from "primeng/tooltip";
import {MessageServiceKey} from "../../../../app.component";
import {MessageService} from "primeng/api";
import {MetaParamsForm} from "../../meta-params-form/meta-params-form.component";
import {SelectButtonModule} from "primeng/selectbutton";
import {MetadataField} from "../../../../gen/common-parsing";

@Component({
  selector: 'app-eval-input-form',
  standalone: true,
  imports: [
    ButtonModule,
    CalendarModule,
    CheckboxModule,
    DropdownModule,
    FormsModule,
    InputNumberModule,
    InputTextModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    TooltipModule,
    MetaParamsForm,
    SelectButtonModule
  ],
  templateUrl: './eval-input-form.component.html',
  styleUrl: './eval-input-form.component.css',
})
export class EvalInputFormComponent implements OnInit {

  @Input() inputVariables: { name: string, label?: string, type?: MetadataNodeType, value?: any }[] = [];
  @Input() code: string;
  @Output() onClose = new EventEmitter();
  @Output() meta: EventEmitter<any> = new EventEmitter<any>();
  @Output() inOutChange: EventEmitter<'in' | 'out'> = new EventEmitter<"in" | "out">();
  @Input() disabled = false;

  inOutOptions = [{label: 'Входные', value: "in"}, {label: 'Выходные', value: "out"}];
  isOut: 'in' | 'out' = 'in';

  constructor(private messageService: MessageService) {
  }

  ngOnInit(): void {
  }

  eval(inputVariables: any[]) {
    try {
      this.inputVariables = inputVariables;
      this.validateCode();
      const localMeta: any = {};
      this.inputVariables.forEach(v => {
        localMeta[v.name] = v.value
      })
      const result = new Function(this.code).bind(localMeta)();
      if (isNaN(result)) {
        throw new Error();
      }
      this.messageService.add({
        key: MessageServiceKey.OK,
        severity: 'success',
        summary: 'Результат',
        detail: result ?? 'Ничего'
      });
    } catch (e: any) {
      this.messageService.add({
        key: MessageServiceKey.OK,
        severity: 'warn',
        summary: 'Внимание',
        detail: (e.message && e.message !== '') ? e.message : 'Невалидный код или значения переменных'
      });
    }
  }

  validateCode() {
    if (!this.code || this.code.length === 0) {
      throw new Error('Не задан код для выполнения');
    }
    if (new Set(this.inputVariables.map(v => v.name)).size !== this.inputVariables.length) {
      throw new Error('Имена переменных должны быть уникальными');
    }
  }

  setMeta(event: any) {
    this.meta.emit(event);
  }

  onIsOutChange() {
    this.inOutChange.emit(this.isOut);
  }
}
