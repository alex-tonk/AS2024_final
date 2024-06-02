import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TutorRegistrationFormComponent} from './tutor-registration-form.component';

describe('TutorRegistrationFormComponent', () => {
  let component: TutorRegistrationFormComponent;
  let fixture: ComponentFixture<TutorRegistrationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TutorRegistrationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TutorRegistrationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
