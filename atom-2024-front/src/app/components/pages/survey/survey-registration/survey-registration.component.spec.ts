import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveyRegistrationComponent } from './survey-registration.component';

describe('SurveyRegistrationComponent', () => {
  let component: SurveyRegistrationComponent;
  let fixture: ComponentFixture<SurveyRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurveyRegistrationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SurveyRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
