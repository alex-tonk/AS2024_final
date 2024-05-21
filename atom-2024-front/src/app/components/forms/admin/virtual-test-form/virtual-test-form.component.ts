import {Component, EventEmitter, HostListener, Input, OnInit, Output} from '@angular/core';
import {MessageService} from "primeng/api";
import {lastValueFrom} from "rxjs";
import {DialogModule} from "primeng/dialog";
import {FormsModule} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {CodeEditorComponent} from "../../../common/code-editor/code-editor.component";
import {EvalInputFormComponent} from "../eval-input-form/eval-input-form.component";
import {NgIf} from "@angular/common";
import {StandEndpointDto, StandEndpointTypeDto} from "../../../../gen/atom2024backend-dto";
import {StandService} from "../../../../gen/atom2024backend-controllers";
import {ComputationType} from "../../../../gen/entities-enums";
import {MetadataNodeType} from "../../../../gen/parsing-enums";
import {MetadataField} from "../../../../gen/common-parsing";

@Component({
  selector: 'app-virtual-test-form',
  standalone: true,
  imports: [
    DialogModule,
    FormsModule,
    DropdownModule,
    ButtonModule,
    InputTextModule,
    CodeEditorComponent,
    EvalInputFormComponent,
    NgIf
  ],
  templateUrl: './virtual-test-form.component.html',
  styleUrl: './virtual-test-form.component.css'
})
export class VirtualTestFormComponent implements OnInit {
  visible = true;
  loading = false;
  isEditMode = false;
  virtualEndpoint: Partial<StandEndpointDto> = {};
  standEndpointTypes: StandEndpointTypeDto[];

  get isEvalMode() {
    return this.virtualEndpoint?.standEndpointType != null;
  }
  jsCode: string;
  isOut: 'in' | 'out' = 'in';

  @Input() endpointFormData?: { standId?: number, endpointId?: number };
  @Output() result = new EventEmitter<StandEndpointDto | null>();

  isCodeEditor = true;

  @HostListener('window:resize', ['$event'])
  rerenderEditor() {
    this.isCodeEditor = false;
    setTimeout(() => this.isCodeEditor = true);
  }

  inputVariables: { name: string, label?: string, type?: MetadataNodeType, value?: any }[] = [];
  inputVariablesAsString: string = 'Параметры шаблона';

  constructor(private messageService: MessageService,
              private standService: StandService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      // Резня и т.д.
      this.standEndpointTypes = await lastValueFrom(this.standService.getStandEndpointTypes());
      if (!!this.endpointFormData?.standId) {
        this.virtualEndpoint = await lastValueFrom(this.standService.getStandEndpoint(this.endpointFormData.standId!, this.endpointFormData.endpointId!));
        this.jsCode = this.virtualEndpoint?.jsCode ?? '';
        this.setInputVariables();
        this.isEditMode = true;
        setTimeout(() => this.rerenderEditor());
      }
    } finally {
      this.loading = false;
    }
  }

  async save() {
    this.loading = true;
    try {
      this.virtualEndpoint.jsCode = this.jsCode;
      if (this.isEditMode) {
        await lastValueFrom(this.standService
          .updateVirtualStandEndpoint(this.virtualEndpoint.stand?.id!, this.virtualEndpoint?.id!, this.virtualEndpoint as StandEndpointDto));
      } else {
        await lastValueFrom(this.standService.createVirtualStandEndpoint(this.virtualEndpoint as StandEndpointDto));
      }
      this.messageService.add({severity: 'success', summary: 'Выполнено', detail: 'Моделирование сохранено'});
      this.result.emit(this.virtualEndpoint as StandEndpointDto)
    } finally {
      this.loading = false;
    }
  }

  onHide() {
    this.visible = false;
    this.result.emit(null);
  }

  evalCode() {
    this.rerenderEditor();
  }

  setInputVariables() {
    let res;
    if (this.virtualEndpoint
      && this.virtualEndpoint.standEndpointType) {
      res = Object.keys(
        this.isOut == 'in'
          ? this.virtualEndpoint.standEndpointType.inMeta.fields
          : this.virtualEndpoint.standEndpointType.outMeta.fields
      ).map(field => {
        return {
          name: field,
          label: this.isOut == 'in'
            ? this.virtualEndpoint?.standEndpointType?.inMeta?.fields[field]?.label
            : this.virtualEndpoint?.standEndpointType?.outMeta?.fields[field]?.label,
          type: this.isOut == 'in'
            ? this.virtualEndpoint?.standEndpointType?.inMeta?.fields[field]?.nodeType
            : this.virtualEndpoint?.standEndpointType?.outMeta?.fields[field]?.nodeType
        }
      });
    }
    this.inputVariables = res ?? [];
    this.inputVariablesAsString = this.inputVariables.map(v => v.name).join(', ');
  }

  onMetaChange(event: { [key: string]: MetadataField }) {
    if (this.virtualEndpoint.standEndpointType == null) {
      return;
    }

    if (this.isOut === 'in') {
      this.virtualEndpoint.standEndpointType!.inMeta = {fields: event};
    } else if (this.isOut === 'out') {
      this.virtualEndpoint.standEndpointType!.outMeta = {fields: event};
    }

    this.inputVariablesAsString = Object.keys(event).join(', ');
  }

  protected readonly setTimeout = setTimeout;
}
