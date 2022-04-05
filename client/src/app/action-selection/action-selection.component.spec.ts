import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionSelectionComponent } from './action-selection.component';

describe('ActionSelectionComponent', () => {
  let component: ActionSelectionComponent;
  let fixture: ComponentFixture<ActionSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActionSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
