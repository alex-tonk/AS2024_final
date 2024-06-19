import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyQuestionListComponent } from './survey-question-list.component';

describe('SurveyQuestionListComponent', () => {
  let component: SurveyQuestionListComponent;
  let fixture: ComponentFixture<SurveyQuestionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurveyQuestionListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SurveyQuestionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
