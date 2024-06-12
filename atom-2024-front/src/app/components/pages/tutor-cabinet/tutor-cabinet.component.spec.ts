import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TutorCabinetComponent } from './tutor-cabinet.component';

describe('UserTutorComponent', () => {
  let component: TutorCabinetComponent;
  let fixture: ComponentFixture<TutorCabinetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TutorCabinetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TutorCabinetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
