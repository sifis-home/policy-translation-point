import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RuleFinishComponent } from './rule-finish.component';

describe('RuleFinishComponent', () => {
  let component: RuleFinishComponent;
  let fixture: ComponentFixture<RuleFinishComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RuleFinishComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RuleFinishComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
