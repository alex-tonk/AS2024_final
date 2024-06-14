import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {SurveyDto, SurveyQuestionDto} from "../../../../gen/survey-dto";
import {MessageService, SharedModule} from "primeng/api";
import {MenuModule} from "primeng/menu";
import {SurveyQuestionService, SurveyService} from "../../../../gen/survey-controllers";
import {lastValueFrom} from "rxjs";
import {DragDropModule} from "primeng/dragdrop";
import {SplitterModule} from "primeng/splitter";
import {NgForOf, NgIf} from "@angular/common";
import {OrderListModule} from "primeng/orderlist";
import {TabViewModule} from "primeng/tabview";
import {InputTextareaModule} from "primeng/inputtextarea";
import {Form, FormControl, FormsModule} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";
import {
  SurveyQuestionRegistrationComponent
} from "./survey-question-registration/survey-question-registration.component";
import {SurveyQuestionListComponent} from "./survey-question-list/survey-question-list.component";
import {
  SurveyQuestionPreviewComponent
} from "./survey-question-registration/survey-question-preview/survey-question-preview.component";
import {Dialog, DialogModule} from "primeng/dialog";
import {StepperModule} from "primeng/stepper";
import {Button, ButtonModule} from "primeng/button";
import {MessageServiceKey} from "../../../../app.component";
import {InputTextModule} from "primeng/inputtext";
import {InputNumberModule} from "primeng/inputnumber";
import {SurveyUtilService} from "../../../../services/survey-util.service";

@Component({
  selector: 'app-survey-registration',
  standalone: true,
  imports: [
    MenuModule,
    DragDropModule,
    SharedModule,
    SplitterModule,
    NgIf,
    NgForOf,
    OrderListModule,
    TabViewModule,
    InputTextareaModule,
    FormsModule,
    DropdownModule,
    SurveyQuestionRegistrationComponent,
    SurveyQuestionListComponent,
    SurveyQuestionPreviewComponent,
    DialogModule,
    StepperModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule
  ],
  templateUrl: './survey-registration.component.html',
  styleUrl: './survey-registration.component.css'
})
export class SurveyRegistrationComponent implements OnInit {

  @Input()
  surveyId?: number;

  @Output()
  result: EventEmitter<SurveyDto | null> = new EventEmitter<SurveyDto | null>();

  @ViewChild('surveyForm')
  surveyForm: FormControl;

  survey: SurveyDto = new SurveyDto();
  selectedQuestion?: SurveyQuestionDto;
  visible = true;
  loading = false;

  stepperIndex = 0;
  tabviewIndex = 0;
  questionRegistrationLoading = false;

  constructor(private surveyQuestionService: SurveyQuestionService,
              private surveyService: SurveyService,
              private messageService: MessageService,
              protected surveyUtilService: SurveyUtilService) {
  }

  async ngOnInit(): Promise<void> {
    this.loading = true;
    try {
      if (this.surveyId != null) {
        const survey = await lastValueFrom(this.surveyService.getSurvey(this.surveyId));
        if (survey == null) {
          this.messageService.add(
            {
              severity: 'error',
              summary: 'Ошибка',
              detail: 'Тестирование не найдено',
              key: MessageServiceKey.OK,
              sticky: true
            }
          );
          this.result.emit(null);
        }
        this.survey = survey;
        this.survey.questions = await lastValueFrom(this.surveyQuestionService.getSurveyQuestionsWithAnswers(this.surveyId));
      } else {
        this.survey = new SurveyDto();
        this.survey.questions = [];
      }
      this.selectedQuestion = this.survey.questions[0];
    } catch (e) {
      this.result.emit(null);
      throw e;
    } finally {
      this.loading = false;
    }
  }

  nextStep() {
    this.stepperIndex += 1;
  }

  previousStep() {
    this.stepperIndex -= 1;
  }

  surveyFormInvalid() {
    return !!this.surveyForm?.invalid;
  }

  surveyQuestionsInvalid() {
    if ((this.survey?.questions ?? []).length == 0) {
      return true;
    }
    for (let question of (this.survey?.questions ?? [])) {
      if (this.surveyUtilService.surveyQuestionInvalidReason(question) != null) {
        return true;
      }
    }
    return false;
  }
}
