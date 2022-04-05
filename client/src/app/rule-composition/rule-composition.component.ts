import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { HttpService } from '../http.service';
import { User } from '../user';
import { AuthService } from '../auth.service';
import { Rule } from '../rule';
import { NotificationsService } from 'angular2-notifications';
import { RuleProblems } from '../rule-problems';
import { RuleService } from '../rule.service'
import { Subscription } from 'rxjs';
import * as $ from 'jquery';

@Component({
  selector: 'app-rule-composition',
  templateUrl: './rule-composition.component.html',
  styleUrls: ['./rule-composition.component.css']
})
export class RuleCompositionComponent implements OnInit {
  level: number;
  type: string;
  user: User;
  recommendedRules: Rule[];
  selected: Rule;
  ruleProblems: RuleProblems;
  other_location_trigger:string;
  other_location_action:string;
  start:Boolean;

  private getRecommendationsRef: Subscription = null;
  private clearRecommendationsRef: Subscription = null;


  @ViewChild('compositionModal') compositionModal;

  constructor(private router: Router, private route: ActivatedRoute, private httpService : HttpService, private ruleService: RuleService,private _notificationsService: NotificationsService, private authService : AuthService) {
  }

  ngOnInit() {
    this.user = this.authService.getAuthUser();
    this.start = true;

    if (!this.user)
      this.router.navigate(['login']);
    else{

      this.route.params.subscribe(params => {
         this.type = params['type'];
         
      });


    }
  }

  selectTriggerService(): void {
    if(this.type == "random")
      this.type = "recrules";
    this.router.navigate(['composition/' + this.type + '/if']);
  }

  selectRule(rule){
    this.selected = rule;
    this.openModal();
  }

  openModal() {
    ($(this.compositionModal.nativeElement) as any).modal('show');
  }
  closeModal() {
    ($(this.compositionModal.nativeElement) as any).modal('hide');
  }

  
  completeRule(): void{
    console.log("completeRule");
    this.closeModal();
    this.ruleService.setTimestamp(new Date().getTime());
    this.ruleService.setType("recommended-rule");
    if(this.type == "random")
      this.type = "recrules";
    for(let td of this.selected.trigger.details){
      if(!td.value)
        td.value = "-";
      if(td.type == 'location'){
        if(td.value = "Other") {
          if(this.other_location_trigger)
            td.value = this.other_location_trigger;
        }
      }
    }

    for(let ad of this.selected.action.details){
      if(!ad.value)
        ad.value = "-";
      if(ad.type == 'location'){
        if(ad.value = "Other") {
          if(this.other_location_action)
            ad.value = this.other_location_trigger;
        }
      }
    }

    this.ruleService.setRule(this.selected);

    this.httpService.saveRule(this.ruleService.getRule(), this.type).subscribe((res :RuleProblems)=> {

      this.ruleProblems = res;
      this.ruleService.setRule(this.ruleProblems.rule);

      if(this.ruleProblems.loops || this.ruleProblems.inconsistentRules || this.ruleProblems.redundantRules){
        //open confirmation dialog
        this.ruleService.setRuleProblems(this.ruleProblems);
        this.router.navigate(['composition/'+ this.type + '/problems']);
        //this.openModal();
      }
      else{
        this._notificationsService.success("Rule saved", "", "yes");
        console.log("saved");
        //this.router.navigate(['composition/'+ this.type + '/list']);
      }
    }, error =>{
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");
    });
  }


}
