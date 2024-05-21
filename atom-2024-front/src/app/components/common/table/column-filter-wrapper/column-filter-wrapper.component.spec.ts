import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ColumnFilterWrapperComponent} from './column-filter-wrapper.component';

describe('ColumnFilterWrapperComponent', () => {
  let component: ColumnFilterWrapperComponent;
  let fixture: ComponentFixture<ColumnFilterWrapperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColumnFilterWrapperComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ColumnFilterWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
