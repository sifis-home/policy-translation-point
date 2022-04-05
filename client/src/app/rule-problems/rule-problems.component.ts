import { Component, OnInit, ViewChild } from '@angular/core';
import { RuleService } from '../rule.service'
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { NotificationsService } from 'angular2-notifications';
import { HttpService } from '../http.service';
import { Service } from '../service';
import { Trigger } from '../trigger';
import { Action } from '../action';
import { Detail } from '../detail';
import { User } from '../user';
import { AuthService } from '../auth.service';
import { RuleProblems } from '../rule-problems';
import { ProblemService } from '../problem.service';
import { Rule } from '../rule';
import * as $ from 'jquery';
@Component({
  selector: 'app-rule-problems',
  templateUrl: './rule-problems.component.html',
  styleUrls: ['./rule-problems.component.css']
})
export class RuleProblemsComponent implements OnInit {
  user: User;
  ruleProblems: RuleProblems;
  rule: Rule;
  type: string;
  enabled : Rule[];
  inconsistent: boolean;
  redundant: boolean;
  loop: boolean;


  constructor(private ruleService : RuleService,  private location: Location, private router: Router, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService, private problemService:ProblemService) { }

  ngOnInit() {
    this.inconsistent = false;
    this.redundant = false;
    this.enabled = [];
    this.user = this.authService.getAuthUser();
    if (!this.user)
      this.router.navigate(['login']);
    else{
      this.rule = this.ruleService.getRule();

      this.ruleProblems = this.ruleService.getRuleProblems();


      this.route.params.subscribe(params => {
        this.type = params['type'];
      });
    }
  }

  setEnabled(rules){

    this.enabled = [];
    for(let r of rules){
        this.enabled.push(r);
    }
  }


  deleteRule(): void{
    this.httpService.deleteRule(this.ruleProblems.rule.dbId, this.type).subscribe(res => {
      this.router.navigate(['composition/'+ this.type + '/list']);
    },error =>{
      console.log(error);
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");
    });
  }

  openExplanation(): void{
    console.log("test");
    this.problemService.setRule(this.rule);
    if(this.loop){
      this.problemService.setRules(this.enabled);
    }
    else{
      this.problemService.setRules(this.ruleProblems.executedRules);
    }
    this.problemService.setInconsistent(this.inconsistent);
    this.problemService.setRedundant(this.redundant);
    this.problemService.setLoop(this.loop);
    this.problemService.setProblematicRules(this.enabled);
    if(this.loop){
      this.problemService.setStartTrigger(this.enabled[0].trigger);
    }
    else{
      this.problemService.setStartTrigger(this.ruleProblems.startTrigger);
    }
    this.router.navigate(['composition/'+ this.type + '/problems/explanation']);
  }


}
