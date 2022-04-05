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
import * as $ from 'jquery';
@Component({
  selector: 'app-rule-finish',
  templateUrl: './rule-finish.component.html',
  styleUrls: ['./rule-finish.component.css']
})
export class RuleFinishComponent implements OnInit {
  ruleProblems: RuleProblems;
  triggerServices: Service[];
  triggerService: Service;
  actionServices: Service[];
  actionService: Service;
  triggerDetails: Detail[];
  actionDetails: Detail[];
  trigger: Trigger;
  action: Action;
  type: string;
  triggerDetailsStr: string;
  actionDetailsStr: string;
  user: User;

  @ViewChild('confirmationModal') confModal;

  constructor(private ruleService : RuleService,  private location: Location, private router: Router, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService){}
  ngOnInit() {
    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{

      this.actionServices = this.ruleService.getActionServices();
      this.action = this.ruleService.getAction();
      this.actionDetails = this.ruleService.getActionDetails();
      this.actionDetails.sort(function(a,b){
        var nameA = a.name.toUpperCase(); // ignore upper and lowercase
        var nameB = b.name.toUpperCase(); // ignore upper and lowercase
        if (nameA < nameB) {
          return -1;
        }
        if (nameA > nameB) {
          return 1;
        }

        // names must be equal
        return 0;
      });
      this.actionService = this.actionServices[0];

      this.triggerServices = this.ruleService.getTriggerServices();
      this.trigger = this.ruleService.getTrigger();
      this.triggerDetails = this.ruleService.getTriggerDetails();
      this.triggerDetails.sort(function(a,b){
        var nameA = a.name.toUpperCase(); // ignore upper and lowercase
        var nameB = b.name.toUpperCase(); // ignore upper and lowercase
        if (nameA < nameB) {
          return -1;
        }
        if (nameA > nameB) {
          return 1;
        }

        // names must be equal
        return 0;
      });

      this.triggerService = this.triggerServices[0];


      this.route.params.subscribe(params => {
        this.type = params['type'];
        if(this.triggerDetails && this.triggerDetails.length > 0){
          this.triggerDetailsStr = "";
          let i = 0;
          for(let triggerDetail of this.triggerDetails){
            this.triggerDetailsStr+= triggerDetail.name;
            this.triggerDetailsStr+= " - ";
            if(triggerDetail.value.name)
              this.triggerDetailsStr+= triggerDetail.value.name;
            else
              this.triggerDetailsStr+= triggerDetail.value;
            i ++;
            if(i!=this.triggerDetails.length)
              this.triggerDetailsStr+=" | ";
            //else
              //this.triggerDetailsStr+= "]";
          }
        }

        if(this.actionDetails && this.actionDetails.length > 0){
          this.actionDetailsStr = "";
          let i = 0;
          for(let actionDetail of this.actionDetails){
            this.actionDetailsStr+= actionDetail.name;
            this.actionDetailsStr+= " - ";
            if(actionDetail.value.name)
              this.actionDetailsStr+= actionDetail.value.name;
            else
              this.actionDetailsStr+= actionDetail.value;
            i ++;
            if(i!= this.actionDetails.length)
              this.actionDetailsStr+= " | ";
            //else
              //this.actionDetailsStr += "]";
          }
        }
      }
     );
   }
  }

  goBack(): void {
    this.location.back();
  }



  completeRule(): void{
    this.ruleService.setTimestamp(new Date().getTime());
    console.log(this.ruleService.getRule());
    this.httpService.saveRule(this.ruleService.getRule(), this.type).subscribe((res)=> {
      this._notificationsService.success("Policy saved", "", "yes");
      this.router.navigate(['composition/'+ this.type + '/list']);
      
    }, error =>{
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");

    });
  }

}
