import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestListComponent } from './test-list.component';

describe('MachineListComponent', () => {
  let component: TestListComponent;
  let fixture: ComponentFixture<TestListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
