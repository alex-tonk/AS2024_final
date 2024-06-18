import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursePanelComponent } from './course-panel.component';

describe('CoursePanelComponent', () => {
  let component: CoursePanelComponent;
  let fixture: ComponentFixture<CoursePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursePanelComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CoursePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
