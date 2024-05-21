import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvalInputFormComponent } from './eval-input-form.component';

describe('MetaInputFormComponent', () => {
  let component: EvalInputFormComponent;
  let fixture: ComponentFixture<EvalInputFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvalInputFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EvalInputFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
