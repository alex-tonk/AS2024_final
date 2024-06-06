import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyAttemptComponent } from './survey-attempt.component';

describe('SurveyAttemptComponent', () => {
  let component: SurveyAttemptComponent;
  let fixture: ComponentFixture<SurveyAttemptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurveyAttemptComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SurveyAttemptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
