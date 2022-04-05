import { Injectable } from '@angular/core';
import { Rule } from './rule';
import { Trigger } from './trigger';

@Injectable()
export class ProblemService {

  rules: Rule[];
  inconsistent: boolean;
  redundant: boolean;
  loop: boolean;
  rule: Rule;
  problematicRules: Rule[];
  startTrigger:Trigger;

  constructor() { } 

  setRules(rules: Rule[]){
    this.rules = rules;
  }

  setInconsistent(value: boolean){
    this.inconsistent = value;
  }

  setRedundant(value: boolean){
    this.redundant = value;
  }

  setLoop(value: boolean){
    this.loop = value;
  }

  setRule(rule: Rule){
    this.rule = rule;
  }

  setProblematicRules(rules: Rule[]){
    this.problematicRules = rules;
  }

  setStartTrigger(trigger: Trigger){
    this.startTrigger = trigger;
  }

  getRules(): Rule[]{
    return this.rules;
  }

  getInconsistent(): boolean{
    return this.inconsistent;
  }

  getRedundant(): boolean{
    return this.redundant;
  }
  getLoop(): boolean{
    return this.loop;
  }
  getRule(): Rule{
    return this.rule;
  }

  getProblematicRules(): Rule[]{
    return this.problematicRules;
  }

  getStartTrigger(): Trigger{
    return this.startTrigger;
  }

}
