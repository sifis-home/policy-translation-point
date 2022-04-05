import { Injectable } from '@angular/core';
import { Rule } from './rule';
import { Service } from './service';
import { IoTEntity } from './iotentity';
import { RuleProblems } from './rule-problems';

@Injectable()
export class RuleService {
  triggerServices: Service[];
  actionServices: Service[];
  triggerEntities: IoTEntity[];
  actionEntities: IoTEntity[];
  rule: Rule;
  ruleProblems: RuleProblems;

  timestamp: number;
  type:string;
  scenarioType:string;


  constructor() {
    this.rule = new Rule();
  }


  getRuleProblems(){
    let problems = this.ruleProblems;
    if(!problems){
      problems = JSON.parse(localStorage.getItem("rule-problems"));
      this.setRuleProblems(problems);
    }
    return problems;
  }

  setType(type) {
    this.type = type;
    localStorage.setItem("type", JSON.stringify(type));
  }

  setScenarioType(setScenarioType){
    this.scenarioType = setScenarioType;
    localStorage.setItem("scenarioType", JSON.stringify(setScenarioType));
  }

  getScenarioType(){
    let t = this.scenarioType;
    if(!t){
      t = JSON.parse(localStorage.getItem("scenarioType"));
    }
    return t;
  }

  setTimestamp(timestamp) {
    this.timestamp = timestamp;
    localStorage.setItem("timestamp", JSON.stringify(timestamp));
  }

  setRuleProblems(problems){
    this.ruleProblems = problems;
    localStorage.setItem("rule-problems",JSON.stringify(this.ruleProblems));
  }

  setTriggerEntities(entities){
    this.triggerEntities = entities;
    localStorage.setItem("trigger-entities",JSON.stringify(this.triggerEntities));
  }

  setActionEntities(entities){
    this.actionEntities = entities;
    localStorage.setItem("action-entities",JSON.stringify(this.actionEntities));
  }

  getTriggerEntities(){
    let entities = this.triggerEntities;
    if(!entities){
      entities = JSON.parse(localStorage.getItem("trigger-entities"));
      this.setTriggerEntities(entities);
    }
    return entities;
  }

  getActionEntities(){
    let entities = this.actionEntities;
    if(!entities){
      entities = JSON.parse(localStorage.getItem("action-entities"));
      this.setActionEntities(entities);
    }
    return entities;
  }

  setTriggerServices(triggerServices){
    this.triggerServices = triggerServices;
    localStorage.setItem("trigger-services",JSON.stringify(this.triggerServices));
  }

  getTriggerServices(){
    let services = this.triggerServices;
    if(!services){
      services = JSON.parse(localStorage.getItem("trigger-services"));
      this.setTriggerServices(services);
    }
    return services;
  }

  setTrigger(trigger){
    this.rule.trigger = trigger;
    localStorage.setItem("trigger-trigger", JSON.stringify(trigger));
  }

  getTrigger(){
    let tr = this.rule.trigger;
    if(!tr){
      this.restoreTrigger();
    }
    return this.rule.trigger;
  }

  setTriggerDetails(details){
    this.rule.trigger.details = details;
    localStorage.setItem("trigger-details", JSON.stringify(details));
  }

  getTriggerDetails(){
    let details = this.rule.trigger.details;
    if(!details){
      this.restoreTrigger();
    }
    return this.rule.trigger.details;
  }

  setActionServices(actionServices){
    this.actionServices = actionServices;
    localStorage.setItem("action-services",JSON.stringify(this.actionServices));
  }

  getActionServices(){
    let services = this.actionServices;
    if(!services){
      services = JSON.parse(localStorage.getItem("action-services"));
      this.setActionServices(services);
    }
    return services;
  }

  setAction(action){
    this.rule.action = action;
    localStorage.setItem("action-action", JSON.stringify(action));
  }

  getAction(){
    let ac = this.rule.action;
    if(!ac){
      this.restoreTrigger();
      this.restoreAction();
    }
    return this.rule.action;
  }

  setActionDetails(details){
    this.rule.action.details = details;
    localStorage.setItem("action-details", JSON.stringify(details));
  }

  getActionDetails(){
    let details = this.rule.action.details;
    if(!details){
      this.restoreTrigger();
      this.restoreAction();
    }
    return this.rule.action.details;
  }

  setTriggerService(service){
    this.rule.trigger.service = service;
    localStorage.setItem("trigger-service", JSON.stringify(service));
  }

  getTriggerService(){
    let service = this.rule.trigger.service;
    if(!service){
      this.restoreTrigger();
    }
    return this.rule.trigger.service;
  }

  setActionService(service){
    this.rule.action.service = service;
    localStorage.setItem("action-service", JSON.stringify(service));
  }

  getActionService(){
    let service = this.rule.action.service;
    if(!service){
      this.restoreTrigger();
      this.restoreAction();
    }
    return this.rule.action.service;
  }

  clear(){
    localStorage.clear();
    this.rule = new Rule();
  }

  getRule(){
    console.log("get rule");
    if(!this.rule.trigger || !this.rule.trigger.service || !this.rule.trigger.details)
      this.restoreTrigger();
    if(!this.rule.action || !this.rule.action.service || !this.rule.action.details)
      this.restoreAction();
    if(!this.rule.dbId)
      this.restoreId();

    this.rule.timestamp = this.timestamp;
   
    if(!this.rule.timestamp)
      this.restoreTimestamp();
   


    return this.rule;
  }

  setRule(rule){
    this.rule = rule;
    localStorage.setItem("rule-id", JSON.stringify(rule.dbId));
  }

  restoreTrigger(){
    this.rule.trigger = JSON.parse(localStorage.getItem("trigger-trigger"));
    //this.rule.trigger.service = JSON.parse(localStorage.getItem("trigger-service"));
    this.rule.trigger.details = JSON.parse(localStorage.getItem("trigger-details"));
  }
  restoreAction(){
    this.rule.action = JSON.parse(localStorage.getItem("action-action"));
    //this.rule.action.service = JSON.parse(localStorage.getItem("action-service"));
    this.rule.action.details = JSON.parse(localStorage.getItem("action-details"));
  }

  restoreId(){
    this.rule.dbId = JSON.parse(localStorage.getItem("rule-id"));
  }

  restoreTimestamp(){
    this.rule.timestamp = JSON.parse(localStorage.getItem("timestamp"));
  }

 
}
