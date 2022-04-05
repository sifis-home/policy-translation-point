import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RuleStepComponent } from './rule-step.component';

describe('RuleStepComponent', () => {
  let component: RuleStepComponent;
  let fixture: ComponentFixture<RuleStepComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RuleStepComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RuleStepComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
