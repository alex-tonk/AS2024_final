import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskAttemptComponent } from './task-attempt.component';

describe('TaskAttemptComponent', () => {
  let component: TaskAttemptComponent;
  let fixture: ComponentFixture<TaskAttemptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskAttemptComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TaskAttemptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
