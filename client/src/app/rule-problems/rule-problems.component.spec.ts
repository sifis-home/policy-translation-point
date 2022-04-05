import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RuleProblemsComponent } from './rule-problems.component';

describe('RuleProblemsComponent', () => {
  let component: RuleProblemsComponent;
  let fixture: ComponentFixture<RuleProblemsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RuleProblemsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RuleProblemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
