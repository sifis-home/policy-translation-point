import { Component, OnInit,ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { RuleService } from '../rule.service'
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationsService } from 'angular2-notifications';
import { HttpService } from '../http.service';
import { Service } from '../service';
import { Trigger } from '../trigger';
import { Detail } from '../detail';
import { User } from '../user';
import { AuthService } from '../auth.service';
import { RecommendationsService } from '../recommendations.service'
import { Rule } from '../rule';
import { Action } from '../action';

@Component({
  selector: 'app-rule-step',
  templateUrl: './rule-step.component.html',
  styleUrls: ['./rule-step.component.css']
})
export class RuleStepComponent implements OnInit {
  triggerServices: Service[];
  triggerService: Service;
  trigger: Trigger;
  triggerName: string;
  triggerId: number;
  type: string;
  user:User;
  other_location:string;


  constructor(private ruleService : RuleService, private location: Location, private router: Router, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService){}

  ngOnInit() {
    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{
      this.triggerServices = this.ruleService.getTriggerServices();
      this.trigger = this.ruleService.getTrigger();
      this.triggerService = this.triggerServices[0];

      this.route.params.subscribe(params => {
         this.type = params['type'];
      });
    }
  }

  goBack(): void {
    this.location.back();
  }

  selectActionService(): void{
    //this.router.navigate(['composition/'+this.type + '/if/' + this.triggerService.id + '/' + this.trigger.id + '/then']);
    this.ruleService.setType("composed");
    this.router.navigate(['composition/'+this.type + '/then']);
  }

}
