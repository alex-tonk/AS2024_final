import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StudentCabinetComponent} from './student-cabinet.component';

describe('UserEducationComponent', () => {
  let component: StudentCabinetComponent;
  let fixture: ComponentFixture<StudentCabinetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentCabinetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentCabinetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
