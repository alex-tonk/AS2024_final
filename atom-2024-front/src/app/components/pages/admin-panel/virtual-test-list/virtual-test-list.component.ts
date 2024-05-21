import {Component, OnInit} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {ButtonModule} from "primeng/button";
import {CheckboxModule} from "primeng/checkbox";
import {
  ColumnFilterWrapperComponent
} from "../../../common/table/column-filter-wrapper/column-filter-wrapper.component";
import {InputTextModule} from "primeng/inputtext";
import {MenuModule} from "primeng/menu";
import {SharedModule} from "primeng/api";
import {TableModule} from "primeng/table";
import {TooltipModule} from "primeng/tooltip";
import {
  UserRegistrationFormComponent
} from "../../../forms/admin/user-registration-form/user-registration-form.component";
import {Column} from "../../../common/table/Column";
import {lastValueFrom} from "rxjs";
import {ExportTable} from "../../../common/table/ExportTable";
import {FormsModule} from "@angular/forms";
import {VirtualTestFormComponent} from "../../../forms/admin/virtual-test-form/virtual-test-form.component";
import {StandEndpointDto} from "../../../../gen/atom2024backend-dto";
import {StandService} from "../../../../gen/atom2024backend-controllers";

@Component({
  selector: 'app-virtual-test-list',
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
    UserRegistrationFormComponent,
    FormsModule,
    VirtualTestFormComponent
  ],
  templateUrl: './virtual-test-list.component.html',
  styleUrl: './virtual-test-list.component.css'
})
export class VirtualTestListComponent implements OnInit {
  virtualTests: StandEndpointDto[] = [];
  selectedEndpoint?: StandEndpointDto;
  loading = false;
  filter = false;

  columns: Column[] = [
    {
      header: 'ID',
      field: 'id',
      type: 'numeric',
      width: 10
    },
    {
      header: 'Тип испытания',
      field: 'name',
      width: 30
    },
    {
      header: 'Описание',
      field: 'description'
    }
  ]

  endpointFormData: { standId?: number, endpointId?: number } | null;

  get columnFields(): string[] {
    const arr = this.columns
      .filter(c => !!c.fieldGetter)
      .map(c => <string>c.fieldGetter);
    const arr2 = this.columns.map(c => c.field);
    return arr.concat(arr2);
  }

  constructor(private standService: StandService) {
  }

  async ngOnInit() {
    await this.initTable();
  }

  async initTable() {
    await this.getVirtualTestsFromApi();
  }

  async getVirtualTestsFromApi() {
    this.virtualTests = [];
    this.selectedEndpoint = undefined;
    this.loading = true;
    try {
      this.virtualTests = await lastValueFrom(this.standService.getVirtualEndpoints())
    } finally {
      this.loading = false;
    }
  }

  createEndpoint() {
    this.endpointFormData = {};
  }

  editEndpoint() {
    this.endpointFormData = {standId: this.selectedEndpoint?.stand.id!, endpointId: this.selectedEndpoint?.id!};
  }

  async onVirtualFormResult(result: StandEndpointDto | null) {
    this.endpointFormData = null;
    if (result) {
      await this.initTable();
    }
  }

  protected readonly ExportTable = ExportTable;
}
