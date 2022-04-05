import { Component, OnInit } from '@angular/core';
import { HttpService } from '../http.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { RuleService } from '../rule.service'
import { NotificationsService } from 'angular2-notifications';
import { Service } from '../service';
import { User } from '../user';
import { AuthService } from '../auth.service';
import { Rule } from '../rule';
import { RuleProblems } from '../rule-problems';

@Component({
  selector: 'app-rule-list',
  templateUrl: './rule-list.component.html',
  styleUrls: ['./rule-list.component.css']
})
export class RuleListComponent implements OnInit {
  ruleProblems: RuleProblems;
  type: string;
  user: User;
  rules: Rule[];
  constructor(private router: Router, private ruleService :  RuleService, private location: Location, private route: ActivatedRoute,  private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService) {
    
    this.rules = [];
  }



  ngOnInit() {
    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{
      this.route.params.subscribe(params => {
        this.type = params['type'];
        this.httpService.getRules(this.type).subscribe(res => {

          console.log(res);
          this.rules = res;

        }, error => {
          this._notificationsService.error("OPS, something goes wrong!", "", "yes");
        });
      }
     );
    }
  }

  deleteRule(rule) {
    if(confirm("Are you sure to delete the rule?")) {
      this.httpService.deleteRule(rule.dbId,this.type).subscribe((res :number)=> {
        this.httpService.getRules(this.type).subscribe(res => {
           this.rules = res;
        }, error => {
          this._notificationsService.error("OPS, something goes wrong!", "", "yes");
        });
        this._notificationsService.success("Rule deleted", "", "yes");
        this.router.navigate(['composition/'+ this.type + '/list']);
      }, error =>{
        this._notificationsService.error("OPS, something goes wrong!", "", "yes");
      });
    }
  }


  newRule(){
    this.ruleService.clear();
    this.router.navigate(['composition/'+ this.type]);
  }

  translate(rule){
    this.httpService.translateRule(rule,this.type).subscribe((res)=> {
      this._notificationsService.success("Policy succesfully translated!", "", "yes");
    });
  }
  
  check(rule){

    this.httpService.checkRule(rule, this.type).subscribe((res: RuleProblems)=> {
      this.ruleProblems = res;
      this.ruleService.setRule(this.ruleProblems.rule);
  
      if(this.ruleProblems.loops || this.ruleProblems.inconsistentRules || this.ruleProblems.redundantRules){
        //open confirmation dialog
        this.ruleService.setRuleProblems(this.ruleProblems);
        this.router.navigate(['composition/'+ this.type + '/problems']);
  
        //this.openModal();
      } else{
        this._notificationsService.success("No problems detected!", "", "yes");
      }
      
    }, error =>{
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");
    });
    
  }
}
