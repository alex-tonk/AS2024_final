import {Component, Input, OnInit} from '@angular/core';
import {SurveyUtilService} from "../../../../../../services/survey-util.service";
import {Model, SurveyNG} from 'survey-angular';
import {SurveyQuestionDto} from '../../../../../../models/survey-dto';

@Component({
  selector: 'app-survey-question-preview',
  standalone: true,
  imports: [],
  templateUrl: './survey-question-preview.component.html',
  styleUrl: './survey-question-preview.component.css'
})
export class SurveyQuestionPreviewComponent implements OnInit {

  @Input()
  set question(value: SurveyQuestionDto) {
    this._question =value;
    this.ngOnInit();
  }
  get question(): SurveyQuestionDto {
    return this._question;
  }

  _question: SurveyQuestionDto;
  surveyModel: Model;

  constructor(private surveyUtilService: SurveyUtilService) {
  }

  ngOnInit(): void {
    const surveyJson = {
      width: '95%',
      elements: this.surveyUtilService.toSurveyJson(this.question)
    };
    this.surveyModel = new Model(surveyJson);
    this.surveyModel.showNavigationButtons = false;
    this.surveyModel.locale = 'ru';
    this.surveyModel.applyTheme(this.surveyUtilService.getThemeJson());
    SurveyNG.render('surveyJsQuestionPreview', {model: this.surveyModel});
  }

}
