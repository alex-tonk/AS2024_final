import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PresentationLessonsComponent} from './presentation-lessons.component';

describe('PresentationLessonsComponent', () => {
  let component: PresentationLessonsComponent;
  let fixture: ComponentFixture<PresentationLessonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PresentationLessonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PresentationLessonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
