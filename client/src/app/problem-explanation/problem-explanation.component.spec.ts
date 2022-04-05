import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProblemExplanationComponent } from './problem-explanation.component';

describe('ProblemExplanationComponent', () => {
  let component: ProblemExplanationComponent;
  let fixture: ComponentFixture<ProblemExplanationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProblemExplanationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProblemExplanationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
