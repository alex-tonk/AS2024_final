import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyQuestionPreviewComponent } from './survey-question-preview.component';

describe('SurveyQuestionPreviewComponent', () => {
  let component: SurveyQuestionPreviewComponent;
  let fixture: ComponentFixture<SurveyQuestionPreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurveyQuestionPreviewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SurveyQuestionPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
