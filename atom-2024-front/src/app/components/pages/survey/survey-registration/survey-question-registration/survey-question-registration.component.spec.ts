import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyQuestionRegistrationComponent } from './survey-question-registration.component';

describe('SurveyQuestionRegistrationComponent', () => {
  let component: SurveyQuestionRegistrationComponent;
  let fixture: ComponentFixture<SurveyQuestionRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurveyQuestionRegistrationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SurveyQuestionRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
