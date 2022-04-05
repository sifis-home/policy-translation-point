import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionChannelComponent } from './action-channel.component';

describe('ActionChannelComponent', () => {
  let component: ActionChannelComponent;
  let fixture: ComponentFixture<ActionChannelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActionChannelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionChannelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
