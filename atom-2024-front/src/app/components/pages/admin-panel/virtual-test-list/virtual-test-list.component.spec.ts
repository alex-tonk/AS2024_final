import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VirtualTestListComponent } from './virtual-test-list.component';

describe('VirtualTestListComponent', () => {
  let component: VirtualTestListComponent;
  let fixture: ComponentFixture<VirtualTestListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirtualTestListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VirtualTestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
