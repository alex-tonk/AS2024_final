import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {DataViewModule} from "primeng/dataview";
import {NgForOf, NgIf, NgTemplateOutlet} from "@angular/common";
import {ButtonModule} from "primeng/button";
import {TagModule} from "primeng/tag";
import {ProgressBarModule} from "primeng/progressbar";
import {TooltipModule} from "primeng/tooltip";
import {firstValueFrom} from "rxjs";
import {LoaderComponent} from "../../common/loader/loader.component";
import {UserRegistrationFormComponent} from "../../forms/admin/user-registration-form/user-registration-form.component";
import {TestRegistrationFormComponent} from "../../forms/user/test-registration-form/test-registration-form.component";
import {TabViewModule} from "primeng/tabview";
import {UserRegistrationComponent} from "../login/user-registration/user-registration.component";
import {MessageService} from "primeng/api";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {SelectButtonModule} from "primeng/selectbutton";
import {TestGroupService} from "../../../gen/atom2024backend-controllers";
import {TestDto, TestGroupDto} from "../../../gen/atom2024backend-dto";
import {TestStatus} from "../../../gen/entities-enums";
import {DateFormatter} from "../../common/DateFormatter";
import {TestGroupFilterEnum} from "../../../gen/dto-enums";
import {DropdownModule} from "primeng/dropdown";
import html2canvas from "html2canvas";
import {MetadataNodeType} from "../../../gen/parsing-enums";
import {ResultLocaleEnum} from "../../../models/ResultLocaleEnum";
import {SampleTypeLocaleEnum} from "../../../models/SampleTypeLocaleEnum";

@Component({
  selector: 'app-test-list',
  standalone: true,
  imports: [
    DataViewModule,
    NgForOf,
    ButtonModule,
    TagModule,
    ProgressBarModule,
    NgIf,
    TooltipModule,
    LoaderComponent,
    UserRegistrationFormComponent,
    TestRegistrationFormComponent,
    TabViewModule,
    UserRegistrationComponent,
    NgTemplateOutlet,
    InputTextModule,
    FormsModule,
    SelectButtonModule,
    DropdownModule
  ],
  templateUrl: './test-list.component.html',
  styleUrl: './test-list.component.css'
})
export class TestListComponent implements OnInit, OnDestroy {
  testGroups: TestGroupDto[] = [];
  loading = false;
  isHiddenMode = false;
  createDialogVisible = false;
  isMultiple = false;

  interval: number;
  globalFilterValue = '';
  filterByStatusOptions = [
    {value: TestGroupFilterEnum.IN_PROGRESS, label: 'В процессе'},
    {value: TestGroupFilterEnum.STOPPED, label: 'Остановленные'},
    {value: TestGroupFilterEnum.ERRORS, label: 'Ошибка'},
    {value: TestGroupFilterEnum.DONE, label: 'Завершенные'},
    {value: TestGroupFilterEnum.ALL, label: 'Все'}
  ];
  filterByStatus = TestGroupFilterEnum.ALL;
  isMobileMode = false;


  get filteredTests(): TestGroupDto[] {
    let groups = this.testGroups;
    if (!!this.globalFilterValue && this.globalFilterValue.length > 0) {
      groups = groups.filter(test => JSON.stringify(test, (key, value) => {
        try {
          return DateFormatter.format(DateFormatter.getDateWithZone(new Date(value)));
        } catch (e) {
          return value;
        }
      }).toLowerCase().includes(this.globalFilterValue.toLowerCase()))
    }
    return groups
  }

  constructor(
    private testGroupService: TestGroupService,
    private messageService: MessageService
  ) {
  }


  @HostListener('window:resize', ['$event'])
  checkMobile() {
    this.isMobileMode = window.innerWidth < 1300;
  }

  async ngOnInit(): Promise<void> {
    this.checkMobile();
    await this.getTestsFromApi();
    this.interval = setInterval(async () => {
      const refreshedGroups = await firstValueFrom(this.testGroupService.getTestGroups());
      this.testGroups
        .filter(group => group.isBusy)
        .forEach(group => {
          const target = refreshedGroups.find(g => g.id === group.id);
          Object.assign(group, target);
          if (target && !target.isBusy) {
            this.messageService.add({
              severity: target.hasError ? 'warn' : 'success',
              summary: 'Выполнено',
              detail: `Испытание ${this.getLabelForGroup(group)} завершено ${target.hasError ? 'с ошибкой' : ''}`
            });
          }
        })
    }, 10000);
  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }

  async getTestsFromApi() {
    this.loading = true;
    this.testGroups = [];
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      this.testGroups = await firstValueFrom(this.testGroupService.getTestGroups(this.filterByStatus));
    } finally {
      this.loading = false;
    }
  }

  async copyTestGroup(group: TestGroupDto) {
    this.loading = true;
    try {
      const res = await firstValueFrom(this.testGroupService.copyTestGroup(group));
      this.testGroups.unshift(res);
      this.messageService.add({
        severity: 'success',
        summary: 'Выполнено',
        detail: `Испытание ${this.getLabelForGroup(group)} повторено`
      });
    } finally {
      this.loading = false;
    }
  }

  async cancelTestGroup(group: TestGroupDto) {
    this.loading = true;
    try {
      const res = await firstValueFrom(this.testGroupService.cancelTest(group.id));
      Object.assign(group, res);
      this.messageService.add({
        severity: 'success',
        summary: 'Выполнено',
        detail: `Испытание ${this.getLabelForGroup(group)} остановлено`
      });
    } finally {
      this.loading = false;
    }
  }

  getLabel(group: TestGroupDto): string {
    if (group.isBusy) {
      return 'В процессе';
    }
    if (group.hasError) {
      return 'Есть ошибки';
    }
    if (group.isStopped) {
      return 'Остановлено';
    }
    return 'Завершено';
  }

  getSeverity(group: TestGroupDto): string {
    if (group.isBusy) {
      return 'warning';
    }
    if (group.hasError) {
      return 'danger';
    }
    if (group.isStopped) {
      return 'danger';
    }
    return 'success';
  }

  getIcon(testItem: TestDto): string {
    switch (testItem.testStatus) {
      case TestStatus.EXECUTING:
        return 'pi pi-spin pi-spinner';
      case TestStatus.ERROR:
        return 'pi pi-exclamation-triangle';
      case TestStatus.CANCELLED:
        return 'pi pi-stop-circle';
      case TestStatus.REGISTERED:
      case TestStatus.UNREGISTERED:
        return '';
      default:
        return 'pi pi-check-circle';
    }
  }

  async onCreateTestResult(event: any) {
    this.createDialogVisible = false;
    if (event !== null) {
      await this.getTestsFromApi();
    }
  }

  getLabelForGroup(group: TestGroupDto) {
    return `${group.comment ?? 'Испытание'} [${group.id}]`;
  }

  getProductLabel(group: TestGroupDto) {
    return 'ДСЕ: ' + Array.from(new Set(group.tests.map(t => t.product.caption)).keys()).join(', ')
  }

  getTypeLabel(group: TestGroupDto) {
    let res = Array.from(new Set(group.tests.map(t => t.standEndpoint.name)).keys());
    return res.length > 1 ? `Стенды: ${res.join(', ')}` : `Стенд: ${res.join(', ')}`
  }

  getDate(date: Date) {
    return date == null ? null : DateFormatter.format(DateFormatter.getDateWithZone(date));
  }

  parseInTestData(test: TestDto): any[] {
    return Array.from(Object.keys(test.inData))
      .map(key => {
        let inData = test.inData as any;
        let inMeta = test.standEndpoint.standEndpointType.inMeta.fields;
        return {label: inMeta[key].label, value: inData[key]}
      });
  }

  parseOutTestData(test: TestDto): any[] {
    if (test.outData != null && typeof test.outData === 'object') {
      return Array.from(Object.keys(test.outData))
        .map(key => {
          let inData = test.outData as any;
          let outMeta = test.standEndpoint.standEndpointType.outMeta.fields;
          let value = inData[key];
          if (outMeta[key] != null && outMeta[key].nodeType === MetadataNodeType.RESULT_ENUM) {
            // @ts-ignore
            value = ResultLocaleEnum[value];
          }
          if (outMeta[key] != null && outMeta[key].nodeType === MetadataNodeType.SAMPLE_TYPE_ENUM) {
            // @ts-ignore
            value = SampleTypeLocaleEnum[value];
          }
          return {label: outMeta[key]?.label ?? key, value: value}
        });
    }
    return [{label: 'Значение', value: test.outData}]
  }

  getTestStatusLabel(testItem: TestDto): string {
    switch (testItem.testStatus) {
      case TestStatus.EXECUTING:
        return 'Выполняется';
      case TestStatus.ERROR:
        return 'Есть ошибки';
      case TestStatus.CANCELLED:
        return 'Остановлено';
      case TestStatus.REGISTERED:
        return 'Зарегистрировано';
      case TestStatus.UNREGISTERED:
        return 'Не зарегистрировано';
      default:
        return 'Завершено';
    }
  }

  getTestSeverity(testItem: TestDto): string {
    switch (testItem.testStatus) {
      case TestStatus.EXECUTING:
        return 'warning';
      case TestStatus.ERROR:
        return 'danger';
      case TestStatus.CANCELLED:
        return 'danger';
      case TestStatus.REGISTERED:
        return 'info';
      case TestStatus.UNREGISTERED:
        return '';
      default:
        return 'success';
    }
  }

  getTestType(testItem: TestDto): string {
    return testItem.standEndpoint.stand.computationType === 'EMULATED' ? 'Моделирование' : 'Стендовое испытание'
  }

  printCard(testGroup: TestGroupDto) {
    this.loading = true;
    this.isHiddenMode = true;
    (testGroup as any).printMode = true;
    try {
      import('jspdf').then((jsPDF) => {
        const printContent = document.getElementById('test-card-' + testGroup.id);
        if (printContent) {
          html2canvas(printContent).then(canvas => {
            const doc = new jsPDF.default('portrait', 'px',
              [printContent.clientHeight, printContent.clientWidth]);
            const imgData = canvas.toDataURL();
            doc.addImage(imgData, 0, 0, printContent.clientWidth, printContent.clientHeight);
            doc.save('print_test_group_' + testGroup.id + '.pdf');
          })
        }
      });
    } finally {
      setTimeout(() =>{
        (testGroup as any).printMode = false;
        this.loading = false;
        this.isHiddenMode = false;
      }, 1000)
    }
  }
}
