import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TriggerChannelComponent } from './trigger-channel.component';

describe('TriggerChannelComponent', () => {
  let component: TriggerChannelComponent;
  let fixture: ComponentFixture<TriggerChannelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TriggerChannelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggerChannelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
