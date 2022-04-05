import { Component, OnInit,  } from '@angular/core';
import { RuleService } from '../rule.service'
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationsService } from 'angular2-notifications';
import { HttpService } from '../http.service';
import { Service } from '../service';
import { Trigger } from '../trigger';
import { Detail } from '../detail';
import { User } from '../user';
import { AuthService } from '../auth.service';
import { IoTEntity } from '../iotentity';

@Component({
  selector: 'app-trigger-details',
  templateUrl: './trigger-details.component.html',
  styleUrls: ['./trigger-details.component.css']
})
export class TriggerDetailsComponent implements OnInit {
  chosenEntity: IoTEntity;
  triggerServices: Service[];
  triggerService: Service;
  trigger: Trigger;
  type: string;
  details: Detail[];
  user:User;
  other_location: string;

  constructor(private ruleService : RuleService,  private location: Location, private router: Router, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService) {

  }

  ngOnInit() {

    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{

      this.triggerServices = this.ruleService.getTriggerServices();
      this.trigger = this.ruleService.getTrigger();
      this.triggerService = this.triggerServices[0];
      this.details = [];

      this.route.params.subscribe(params => {
        this.type = params['type'];

        this.httpService.getTriggerDetails(this.triggerService.id,this.trigger.id,this.type).subscribe(res => {
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

  completeTrigger(): void{
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
    this.ruleService.setTriggerDetails(this.details);
    this.router.navigate(['composition/'+this.type + '/if/check']);
  }
}
