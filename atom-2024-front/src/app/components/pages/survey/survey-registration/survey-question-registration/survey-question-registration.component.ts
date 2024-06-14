import {ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {DropdownChangeEvent, DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {InputTextareaModule} from "primeng/inputtextarea";
import {NgClass, NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault} from "@angular/common";
import {SurveyQuestionDto} from "../../../../../gen/survey-dto";
import {MessageService, SelectItem} from 'primeng/api';
import {SurveyQuestionType} from "../../../../../gen/survey-enums";
import {SurveyQuestionTypeLocaleEnum} from "../../../../../models/SurveyQuestionTypeLocaleEnum";
import {ChipsModule} from "primeng/chips";
import {
  CheckboxSurveyQuestionAnswerMeta,
  NumberSurveyQuestionAnswerMeta,
  PredefinedAnswerMeta,
  PredefinedAnswerQuestionMeta,
  RadioButtonSurveyQuestionAnswerMeta,
  RankingSurveyQuestionAnswerMeta,
  StringSurveyQuestionCorrectAnswerMeta
} from "../../../../../models/SurveyQuestionMeta";
import {InputNumberModule} from "primeng/inputnumber";
import {ButtonModule} from "primeng/button";
import {TooltipModule} from "primeng/tooltip";
import {OrderListModule} from "primeng/orderlist";
import {resetParseTemplateAsSourceFileForTest} from "@angular/compiler-cli/src/ngtsc/typecheck/diagnostics";
import {FileUpload, FileUploadHandlerEvent, FileUploadModule} from 'primeng/fileupload';
import {lastValueFrom} from 'rxjs';
import {FileService} from '../../../../../services/file.service';
import {MessageServiceKey} from '../../../../../app.component';
import FileSaver from 'file-saver';

@Component({
  selector: 'app-survey-question-registration',
  standalone: true,
    imports: [
        DropdownModule,
        FormsModule,
        InputTextareaModule,
        NgIf,
        NgSwitchCase,
        NgSwitch,
        NgSwitchDefault,
        ChipsModule,
        InputNumberModule,
        NgForOf,
        ButtonModule,
        TooltipModule,
        OrderListModule,
        NgClass,
        FileUploadModule
    ],
  templateUrl: './survey-question-registration.component.html',
  styleUrl: './survey-question-registration.component.css'
})
export class SurveyQuestionRegistrationComponent {

  @Input()
  question: SurveyQuestionDto;

  @Output()
  loading: EventEmitter<boolean> = new EventEmitter<boolean>();

  surveyQuestionTypes: SelectItem[] = Object.keys(SurveyQuestionType)
    .map(qT => ({label: SurveyQuestionTypeLocaleEnum[qT as keyof typeof SurveyQuestionTypeLocaleEnum], value: qT}));
  protected readonly SurveyQuestionType = SurveyQuestionType;

  constructor(private fileService: FileService,
              private messageService: MessageService) {
  }

  onQuestionTypeChange(event: DropdownChangeEvent) {
    this.question.meta = {};
    this.question.correctAnswerMeta = {};
    switch (event.value as SurveyQuestionType) {
      case SurveyQuestionType.RADIO_BUTTON:
      case SurveyQuestionType.CHECKBOX:
      case SurveyQuestionType.RANKING:
        this.addPredefinedAnswer();
        break;
    }
  }

  addPredefinedAnswer() {
    switch (this.question.type as SurveyQuestionType) {
      case SurveyQuestionType.RADIO_BUTTON:
      case SurveyQuestionType.CHECKBOX:
      case SurveyQuestionType.RANKING:
        const meta = this.question.meta as PredefinedAnswerQuestionMeta;
        let maxId = 0;
        if ((meta?.answers?.length ?? 0) > 0) {
          maxId = Math.max(...((meta.answers?.map(a => a.id!) ?? [-1]))) + 1;
        }
        console.log(maxId);
        meta.answers = [...(meta.answers ?? []), {id: maxId}];

        if (this.question.type === SurveyQuestionType.RANKING) {
          const rankingMeta = this.question.correctAnswerMeta as RankingSurveyQuestionAnswerMeta;
          rankingMeta.answerIdsOrdered = (meta.answers ?? []).map(a => a.id!);
        }
        break;
    }
  }

  deletePredefinedAnswer(answer: PredefinedAnswerMeta) {
    switch (this.question.type as SurveyQuestionType) {
      case SurveyQuestionType.RADIO_BUTTON:
      case SurveyQuestionType.CHECKBOX:
      case SurveyQuestionType.RANKING:
        const meta = this.question.meta as PredefinedAnswerQuestionMeta;
        meta.answers = (meta.answers ?? []).filter(a => a != answer);
        break;
    }

    switch (this.question.type as SurveyQuestionType) {
      case SurveyQuestionType.RADIO_BUTTON:
        const radioButtonMeta = this.question.correctAnswerMeta as RadioButtonSurveyQuestionAnswerMeta;
        if (radioButtonMeta.answerId === answer.id) {
          radioButtonMeta.answerId = undefined;
        }
        break;
      case SurveyQuestionType.CHECKBOX:
        const checkboxMeta = this.question.correctAnswerMeta as CheckboxSurveyQuestionAnswerMeta;
        checkboxMeta.answerIds = (checkboxMeta.answerIds ?? []).filter(id => id !== answer.id);
        break;
      case SurveyQuestionType.RANKING:
        const rankingMeta = this.question.correctAnswerMeta as RankingSurveyQuestionAnswerMeta;
        rankingMeta.answerIdsOrdered = (rankingMeta.answerIdsOrdered ?? []).filter(id => id !== answer.id);
        break;
    }
  }

  markAnswerAsCorrect(answer: PredefinedAnswerMeta) {
    switch (this.question.type as SurveyQuestionType) {
      case SurveyQuestionType.RADIO_BUTTON:
        const radioButtonMeta = this.question.correctAnswerMeta as RadioButtonSurveyQuestionAnswerMeta;
        if (radioButtonMeta.answerId === answer.id) {
          radioButtonMeta.answerId = undefined;
        } else {
          radioButtonMeta.answerId = answer.id;
        }
        break;
      case SurveyQuestionType.CHECKBOX:
        const checkboxMeta = this.question.correctAnswerMeta as CheckboxSurveyQuestionAnswerMeta;
        if ((checkboxMeta.answerIds ?? []).some(id => id == answer.id)) {
          checkboxMeta.answerIds = (checkboxMeta.answerIds ?? []).filter(id => id !== answer.id);
        } else {
          checkboxMeta.answerIds = [...(checkboxMeta.answerIds ?? []), answer.id!];
        }
        break;
    }
  }

  isCorrectPredefinedAnswer(answer: PredefinedAnswerMeta) {
    switch (this.question.type as SurveyQuestionType) {
      case SurveyQuestionType.RADIO_BUTTON:
        const radioButtonMeta = this.question.correctAnswerMeta as RadioButtonSurveyQuestionAnswerMeta;
        return radioButtonMeta.answerId === answer.id;
      case SurveyQuestionType.CHECKBOX:
        const checkboxMeta = this.question.correctAnswerMeta as CheckboxSurveyQuestionAnswerMeta;
        return (checkboxMeta.answerIds ?? []).some(id => id == answer.id);
    }
    return false;
  }

  movePredefinedAnswerInRanking(answer: PredefinedAnswerMeta, direction: 'up' | 'down') {
    switch (this.question.type as SurveyQuestionType) {
      case SurveyQuestionType.RANKING:
        const meta = this.question.meta as PredefinedAnswerQuestionMeta;
        const fromIndex = (meta.answers ?? []).indexOf(answer);
        if (fromIndex >= 0
          && !(fromIndex == 0 && direction === 'up')
          && !(fromIndex == ((meta.answers ?? []).length - 1) && direction === 'down')) {
          const newAnswers = [...(meta.answers ?? [])];
          const toIndex = direction === 'up' ? fromIndex - 1 : fromIndex + 1;
          newAnswers.splice(fromIndex, 1);
          newAnswers.splice(toIndex, 0, answer);
          meta.answers = newAnswers;
          const rankingMeta = this.question.correctAnswerMeta as RankingSurveyQuestionAnswerMeta;
          rankingMeta.answerIdsOrdered = (meta.answers ?? []).map(a => a.id!);
        }
        break;
    }
  }

  async addSurveyQuestionFile(event: FileUploadHandlerEvent, fileUpload: FileUpload) {
    this.loading.emit(true);
    try {
      this.question.fileName = event.files[0].name;
      this.question.fileId = await lastValueFrom(this.fileService.uploadSurveyQuestionFile(event.files[0]));
    } finally {
      fileUpload.clear();
      this.loading.emit(false);
    }
  }

  async deleteSurveyQuestionFile() {
    if (this.question?.fileId == null) {
      return;
    }
    this.loading.emit(true);
    try {
      const success = await lastValueFrom(this.fileService.deleteSurveyQuestionFile(this.question.fileId));
      if (!success) {
        this.messageService.add(
          {
            severity: 'error',
            summary: 'Ошибка',
            detail: 'Ошибка удаления файла',
            key: MessageServiceKey.OK,
            sticky: true
          }
        );
      }
      this.question.fileName = undefined;
      this.question.fileId = undefined;
    } finally {
      this.loading.emit(false);
    }
  }

  async downloadSurveyQuestionFile() {
    if (this.question?.fileId == null) {
      return;
    }
    this.loading.emit(true);
    try {
      let blob = await lastValueFrom(this.fileService.getSurveyQuestionFile(this.question.fileId));
      FileSaver.saveAs(blob, this.question.fileName);
    } finally {
      this.loading.emit(false);
    }
  }
}
