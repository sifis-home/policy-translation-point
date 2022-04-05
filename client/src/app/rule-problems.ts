import { Rule } from './rule';
import { Action } from './action';
import { Trigger } from './trigger';
export class RuleProblems {
  rule: Rule;
  loops: Rule[][];
  redundantRules: Rule[][];
  inconsistentRules: Rule[][];
  executedRules: Rule[];
  startTrigger: Trigger;

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }
}
