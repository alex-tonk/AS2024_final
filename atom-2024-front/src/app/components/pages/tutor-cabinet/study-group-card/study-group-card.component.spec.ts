import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudyGroupCardComponent } from './study-group-card.component';

describe('StudyGroupCardComponent', () => {
  let component: StudyGroupCardComponent;
  let fixture: ComponentFixture<StudyGroupCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudyGroupCardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StudyGroupCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
