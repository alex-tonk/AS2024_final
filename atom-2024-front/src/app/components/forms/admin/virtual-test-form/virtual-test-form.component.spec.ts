import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualTestFormComponent } from './virtual-test-form.component';

describe('VirtualTestFormComponent', () => {
  let component: VirtualTestFormComponent;
  let fixture: ComponentFixture<VirtualTestFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirtualTestFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VirtualTestFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
