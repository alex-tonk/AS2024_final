import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StudentRegistrationFormComponent} from './student-registration-form.component';

describe('StudentRegistrationFormComponent', () => {
  let component: StudentRegistrationFormComponent;
  let fixture: ComponentFixture<StudentRegistrationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentRegistrationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentRegistrationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
