import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetaParamsForm } from './meta-params-form.component';

describe('ParamsFormComponent', () => {
  let component: MetaParamsForm;
  let fixture: ComponentFixture<MetaParamsForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MetaParamsForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetaParamsForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
