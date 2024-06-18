import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LectureViewComponent } from './lecture-view.component';

describe('LectureViewComponent', () => {
  let component: LectureViewComponent;
  let fixture: ComponentFixture<LectureViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LectureViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LectureViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
