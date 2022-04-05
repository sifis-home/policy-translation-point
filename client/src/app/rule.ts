import { Trigger } from './trigger';
import { Action } from './action';
export class Rule {
  dbId: number;
  trigger: Trigger;
  action: Action;
  hover: Boolean;
  timestamp:number;

  constructor(values: Object = {}) {
    Object.assign(this, values);
    this.hover = false;
  }

}
