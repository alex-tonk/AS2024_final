import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EvalInputFormComponent} from "../../admin/eval-input-form/eval-input-form.component";
import {ProductService, StandService, TestGroupService} from "../../../../gen/atom2024backend-controllers";
import {MessageService} from "primeng/api";
import {lastValueFrom} from "rxjs";
import {DialogModule} from "primeng/dialog";
import {ButtonModule} from "primeng/button";
import {FormsModule} from "@angular/forms";
import {MetaParamsForm} from "../../meta-params-form/meta-params-form.component";
import {DropdownModule} from "primeng/dropdown";
import {NgForOf, NgIf} from "@angular/common";
import {ChipModule} from "primeng/chip";
import {ProductDto, StandEndpointDto, TestDto, TestGroupDto} from "../../../../gen/atom2024backend-dto";
import {MetadataNodeType} from "../../../../gen/parsing-enums";
import {InputTextModule} from "primeng/inputtext";

@Component({
  selector: 'app-test-registration-form',
  standalone: true,
  imports: [
    EvalInputFormComponent,
    DialogModule,
    ButtonModule,
    FormsModule,
    MetaParamsForm,
    DropdownModule,
    NgIf,
    NgForOf,
    ChipModule,
    InputTextModule
  ],
  templateUrl: './test-registration-form.component.html',
  styleUrl: './test-registration-form.component.css'
})
export class TestRegistrationFormComponent implements OnInit {
  visible = true;
  loading = false;
  // TODO Круто было бы все же редактировать черновик
  isEditMode = false;

  test: Partial<TestDto> = {};
  testTypes: StandEndpointDto[];
  products: ProductDto[];

  index = 0;
  testGroup: Partial<TestGroupDto> = {tests: []}
  inputVariables: { name: string, label?: string, type?: MetadataNodeType, value?: any }[] = [];

  @Input() testId?: number;
  @Input() isMultiple = false;
  @Output() result = new EventEmitter<any>();

  get header() {
    return `Введите параметры нового ${this.isMultiple ? 'комбинированного' : ''} испытания`;
  }

  get style() {
    // return this.isMultiple ? {width: '90vw', height: '90vh'} : {width: '600px'};
    return this.isMultiple ? {width: '720px'} : {width: '600px'};
  }

  constructor(private messageService: MessageService,
              private standService: StandService,
              private productService: ProductService,
              private testGroupService: TestGroupService) {
  }

  async ngOnInit() {
    this.loading = true;
    try {
      this.testTypes = await lastValueFrom(this.standService.getAllStandEndpoints());
      this.products = await lastValueFrom(this.productService.getProducts());
      if (this.testId) {
        // TODO ? getTestById()
        this.isEditMode = true;
      }
    } finally {
      this.loading = false;
    }
  }

  async initTest() {
    this.loading = true;
    try {
      if (!this.isMultiple) {
        this.testGroup.tests = [];
        this.addToGroup();
      }
      this.testGroup = await lastValueFrom(this.testGroupService.createTestGroup(this.testGroup as TestGroupDto));
      this.messageService.add({
        severity: 'success', summary: 'Выполнено',
        detail: this.isMultiple ? 'Комбинированное испытание зарегистрировано' : 'Испытание зарегистрировано'
      });
      this.result.emit(this.testGroup)
    } finally {
      this.loading = false;
    }
  }

  addToGroup() {
    this.index++;
    this.testGroup.tests!.push({...this.test} as TestDto);
  }

  onRemove(index: number) {
    this.testGroup.tests!.splice(index, 1);
  }

  onHide() {
    this.visible = false;
    this.result.emit(null);
  }

  setInputVariables() {
    let res;
    if (this.test.standEndpoint && this.test.standEndpoint.standEndpointType) {
      res = Object.keys(this.test.standEndpoint.standEndpointType.inMeta.fields).map(field => {
        return {
          name: field,
          label: this.test.standEndpoint?.standEndpointType?.inMeta.fields[field].label,
          type: this.test.standEndpoint?.standEndpointType?.inMeta.fields[field].nodeType
        }
      })
    }
    this.inputVariables = res ?? [];
  }

  setParams(value: Object) {
    this.test.inData = value;
  }
}
