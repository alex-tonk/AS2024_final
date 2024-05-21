import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestRegistrationFormComponent } from './test-registration-form.component';

describe('TestRegistrationFormComponent', () => {
  let component: TestRegistrationFormComponent;
  let fixture: ComponentFixture<TestRegistrationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestRegistrationFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TestRegistrationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
