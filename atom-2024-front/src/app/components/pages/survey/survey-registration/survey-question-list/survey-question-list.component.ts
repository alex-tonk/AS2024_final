import {ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {OrderListModule} from "primeng/orderlist";
import {SharedModule} from "primeng/api";
import {ButtonModule} from "primeng/button";
import {TooltipModule} from "primeng/tooltip";
import {NgForOf, NgIf, NgSwitch} from "@angular/common";
import {SurveyUtilService} from "../../../../../services/survey-util.service";
import {SurveyQuestionDto} from '../../../../../models/survey-dto';
import {SurveyQuestionType} from '../../../../../models/survey-enums';

@Component({
  selector: 'app-survey-question-list',
  standalone: true,
  imports: [
    OrderListModule,
    SharedModule,
    ButtonModule,
    TooltipModule,
    NgSwitch,
    NgForOf,
    NgIf
  ],
  templateUrl: './survey-question-list.component.html',
  styleUrl: './survey-question-list.component.css'
})
export class SurveyQuestionListComponent {

  @Input()
  set questions(value: SurveyQuestionDto[]) {
    this._questions = value;
    this.onQuestionReorder();
  }

  get questions(): SurveyQuestionDto[] {
    return this._questions;
  }

  @Input()
  set selectedQuestion(value: SurveyQuestionDto | undefined) {
    this.selectedQuestions = value != null ? [value] : [];
  }

  get selectedQuestion(): SurveyQuestionDto | undefined {
    return this.selectedQuestions[0];
  }

  @Output()
  selectedQuestionChange: EventEmitter<SurveyQuestionDto | undefined> = new EventEmitter<SurveyQuestionDto | undefined>();

  @Output()
  questionsChange: EventEmitter<SurveyQuestionDto[]> = new EventEmitter<SurveyQuestionDto[]>()

  _questions: SurveyQuestionDto[] = [];
  selectedQuestions: SurveyQuestionDto[] = [];
  protected readonly Math = Math;

  constructor(private changeDetectorRef: ChangeDetectorRef,
              private surveyUtilService: SurveyUtilService) {
  }

  onQuestionSelectionChange(event: SurveyQuestionDto[]) {
    let newSelectedQuestion = this.selectedQuestion;
    if (event?.length > 0) {
      newSelectedQuestion = event.find(q => q !== this.selectedQuestion);
    }
    if (newSelectedQuestion != this.selectedQuestion) {
      this.selectedQuestion = newSelectedQuestion;
      this.selectedQuestionChange.emit(this.selectedQuestion);
    }
  }

  onQuestionReorder() {
    this.questions.forEach((q, index) => q.orderNumber = index + 1);
  }

  getQuestionIcon(question: SurveyQuestionDto) {
    switch (question.type as SurveyQuestionType) {
      case SurveyQuestionType.STRING:
        return 'pi pi-pencil';
      case SurveyQuestionType.NUMBER:
        return 'pi pi-question';
      case SurveyQuestionType.RADIO_BUTTON:
        return 'pi pi-check-circle';
      case SurveyQuestionType.CHECKBOX:
        return 'pi pi-list';
      case SurveyQuestionType.RANKING:
        return 'pi pi-sort-numeric-up'

    }
  }

  deleteQuestion(question: SurveyQuestionDto) {
    const index = this.questions.indexOf(question);
    if (index >= 0) {
      if (this.selectedQuestion === question) {
        this.selectedQuestion = this.questions[index + 1] ?? this.questions[index - 1];
        this.selectedQuestionChange.emit(this.selectedQuestion);
      }
      this.questions.splice(index, 1);
      this.onQuestionReorder();
    }
  }

  addQuestion() {
    const question = {
      type: SurveyQuestionType.STRING,
      meta: {},
      correctAnswerMeta: {}
    };

    this.questions = [...this.questions, question];
    this.questionsChange.emit(this.questions);
    this.onQuestionReorder();
    this.changeDetectorRef.detectChanges();
    if (this.questions.length == 1) {
      this.selectedQuestion = this.questions[0];
      this.selectedQuestionChange.emit(this.selectedQuestion);
    }
  }

  protected readonly Array = Array;

  questionInvalidReason(question: SurveyQuestionDto): string | null {
    return this.surveyUtilService.surveyQuestionInvalidReason(question);
  }
}
