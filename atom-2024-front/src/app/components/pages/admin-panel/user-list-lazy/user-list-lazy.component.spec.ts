import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserListLazyComponent} from './user-list-lazy.component';

describe('UserListLazyComponent', () => {
  let component: UserListLazyComponent;
  let fixture: ComponentFixture<UserListLazyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserListLazyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserListLazyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
