import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TriggerSelectionComponent } from './trigger-selection.component';

describe('TriggerSelectionComponent', () => {
  let component: TriggerSelectionComponent;
  let fixture: ComponentFixture<TriggerSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TriggerSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggerSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
