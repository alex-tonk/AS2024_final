import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudyGroupRegistrationFormComponent } from './study-group-registration-form.component';

describe('StudyGroupRegistrationFormComponent', () => {
  let component: StudyGroupRegistrationFormComponent;
  let fixture: ComponentFixture<StudyGroupRegistrationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudyGroupRegistrationFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StudyGroupRegistrationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
