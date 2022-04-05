import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TriggerDetailsComponent } from './trigger-details.component';

describe('TriggerDetailsComponent', () => {
  let component: TriggerDetailsComponent;
  let fixture: ComponentFixture<TriggerDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TriggerDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggerDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
