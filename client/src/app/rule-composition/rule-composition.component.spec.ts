import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RuleCompositionComponent } from './rule-composition.component';

describe('RuleCompositionComponent', () => {
  let component: RuleCompositionComponent;
  let fixture: ComponentFixture<RuleCompositionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RuleCompositionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RuleCompositionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
