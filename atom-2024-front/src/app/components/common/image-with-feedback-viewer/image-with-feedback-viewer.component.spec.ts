import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageWithFeedbackViewerComponent } from './image-with-feedback-viewer.component';

describe('ImageWithFeedbackViewerComponent', () => {
  let component: ImageWithFeedbackViewerComponent;
  let fixture: ComponentFixture<ImageWithFeedbackViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImageWithFeedbackViewerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ImageWithFeedbackViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
