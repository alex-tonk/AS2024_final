import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MetadataNodeType} from "../../../gen/parsing-enums";
import {ButtonModule} from "primeng/button";
import {CalendarModule} from "primeng/calendar";
import {CheckboxModule} from "primeng/checkbox";
import {DropdownModule} from "primeng/dropdown";
import {ControlContainer, FormsModule, NgForm} from "@angular/forms";
import {InputNumberModule} from "primeng/inputnumber";
import {InputTextModule} from "primeng/inputtext";
import {NgForOf, NgIf, NgTemplateOutlet} from "@angular/common";
import {TooltipModule} from "primeng/tooltip";
import _default from "chart.js/dist/core/core.interaction";
import index = _default.modes.index;
import {SelectButtonModule} from "primeng/selectbutton";
import {MetadataField} from "../../../gen/common-parsing";
import {ResultLocaleEnum} from "../../../models/ResultLocaleEnum";
import {ResultEnum} from "../../../models/ResultEnum";
import {SampleTypeEnum} from "../../../models/SampleTypeEnum";
import {SampleTypeLocaleEnum} from "../../../models/SampleTypeLocaleEnum";

@Component({
  selector: 'app-meta-params-form',
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
    TooltipModule,
    NgTemplateOutlet,
    SelectButtonModule
  ],
  templateUrl: './meta-params-form.component.html',
  styleUrl: './meta-params-form.component.css',
  viewProviders: [{provide: ControlContainer, useExisting: NgForm}]
})
export class MetaParamsForm implements OnInit {
  inputVariablesCount = 0;

  @Input() isReadOnlyParams = true;

  @Input() set inputVariables(value: { name: string, label?: string, type?: MetadataNodeType, value?: any }[]) {
    this._inputVariables = value;
    this.ngOnInit();
  }

  get inputVariables(): { name: string, label?: string, type?: MetadataNodeType, value?: any }[] {
    return this._inputVariables;
  }

  resultOptions: {label: string, value: ResultEnum}[] = Object.keys(ResultEnum)
    .map(key => {
      // @ts-ignore
      return {label: ResultLocaleEnum[key], value: ResultEnum[key]};
    });

  sampleTypeOptions: {label: string, value: SampleTypeEnum}[] = Object.keys(SampleTypeEnum)
    .map(key => {
      // @ts-ignore
      return {label: SampleTypeLocaleEnum[key], value: SampleTypeEnum[key]};
    });

  @Output() result = new EventEmitter();
  @Output() resultMeta: EventEmitter<{ [key: string]: MetadataField }> = new EventEmitter<{
    [key: string]: MetadataField
  }>();

  _inputVariables: { name: string, label?: string, type?: MetadataNodeType, value?: any }[] = [];

  inputVariableTypes = [
    {label: 'NUMBER', value: MetadataNodeType.NUMBER},
    {label: 'STRING', value: MetadataNodeType.STRING},
    {label: 'DATE', value: MetadataNodeType.DATE},
    {label: 'BOOLEAN', value: MetadataNodeType.BOOLEAN},
  ]

  get variablesCount() {
    return this.inputVariables.length;
  }

  ngOnInit(): void {
    this.inputVariablesCount = this.inputVariables.length;
    if (this.inputVariables.length === 0) {
      this.addVariable();
      this.addVariable();
      this.addVariable();
    }
  }

  getVariableName(): string {
    return `VARIABLE_${this.inputVariablesCount}`;
  }

  getVariableLabel(): string {
    return `Переменная ${this.inputVariablesCount}`;
  }

  addVariable() {
    this.inputVariablesCount++;
    this.inputVariables.push({
      name: this.getVariableName(),
      type: MetadataNodeType.NUMBER,
      label: this.getVariableLabel()
    });
    this.onInputValueChange();
  }

  removeVariable(index: number) {
    this.inputVariables.splice(index, 1);
    this.onInputValueChange();
  }

  onInputValueChange() {
    const obj: any = {};
    this.inputVariables.forEach(v => {
      obj[v.name] = v.value;
    })
    this.result.emit(obj);
    const meta: any = {};
    this.inputVariables.forEach(v => {
      meta[v.name] = {label: v.label, nodeType: v.type};
    });
    this.resultMeta.emit(meta);
  }

  protected readonly MetadataNodeType = MetadataNodeType;
}
