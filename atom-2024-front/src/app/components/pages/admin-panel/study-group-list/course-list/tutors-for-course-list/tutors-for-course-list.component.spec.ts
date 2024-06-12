import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TutorsForCourseListComponent } from './tutors-for-course-list.component';

describe('TutorsForCourseListComponent', () => {
  let component: TutorsForCourseListComponent;
  let fixture: ComponentFixture<TutorsForCourseListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TutorsForCourseListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TutorsForCourseListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
