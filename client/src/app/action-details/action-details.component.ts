import { Component, OnInit } from '@angular/core';
import { RuleService } from '../rule.service'
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { NotificationsService } from 'angular2-notifications';
import { HttpService } from '../http.service';
import { Service } from '../service';
import { Action } from '../action';
import { Detail } from '../detail';
import { User } from '../user';
import { AuthService } from '../auth.service';
import { IoTEntity } from '../iotentity';

@Component({
  selector: 'app-action-details',
  templateUrl: './action-details.component.html',
  styleUrls: ['./action-details.component.css']
})
export class ActionDetailsComponent implements OnInit {
  actionServices: Service[];
  actionService: Service;
  action: Action;
  type: string;
  details: Detail[];
  user:User;
  other_location: string;

  constructor(private ruleService : RuleService,  private location: Location, private router: Router, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService){}

  ngOnInit() {

    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{

      this.actionServices = this.ruleService.getActionServices();
      this.action = this.ruleService.getAction();
      this.actionService = this.actionServices[0];
      this.details = [];

      this.route.params.subscribe(params => {
        this.type = params['type'];

        this.httpService.getActionDetails(this.actionService.id,this.action.id, this.type).subscribe(res => {
          this.details = res;
          this.details.sort(function(a,b){
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
        }, error => {

          this._notificationsService.error("OPS, something goes wrong!", "", "yes");
        });

      }
      );
    }
  }

  goBack(): void {
    this.location.back();
  }

  completeAction(): void{
    for(let d of this.details){


      if(!d.value)
        d.value = "-"
      /*if(d.type == 'location'){
        if(d.value = "Other") {
          if(this.other_location)
            d.value = this.other_location;
        }
      }*/
    }
    console.log(this.details)
    this.ruleService.setActionDetails(this.details);
    this.router.navigate(['composition/'+ this.type + '/then/check']);
  }

}
