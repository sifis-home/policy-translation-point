import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';
import { RuleService } from '../rule.service'
import { NotificationsService } from 'angular2-notifications';
import { HttpService } from '../http.service';
import { Router } from '@angular/router';
import { Detail } from '../detail';
import { Trigger } from '../trigger';
import { User } from '../user';
import { Service } from '../service';
import { AuthService } from '../auth.service';
import { IoTEntity } from '../iotentity';

@Component({
  selector: 'app-trigger-selection',
  templateUrl: './trigger-selection.component.html',
  styleUrls: ['./trigger-selection.component.css']
})
export class TriggerSelectionComponent implements OnInit {
  triggerServices: Service[];
  triggerService: Service;
  triggers: Trigger[];
  type: string;
  user: User;
  connect: boolean = false;

  entityName: string;

  constructor(private ruleService : RuleService, private route: ActivatedRoute, private location: Location, private router: Router, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService) { }

  ngOnInit() {
    this.connect = true;
    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{
      this.triggerServices = this.ruleService.getTriggerServices();
      this.triggerService = this.triggerServices[0];
      this.triggers = [];

      this.route.params.subscribe(params => {
        this.type = params['type'];
        this.connect = false;
        for(let service of this.triggerServices){
          this.httpService.getTriggers(service.id, this.type).subscribe(res => {
            for(let tr of res){
              console.log(tr);
              this.triggers.push(tr);
            }
          }, error => {
            this._notificationsService.error("OPS, something goes wrong!", "", "yes");
          });
        }
        
        /*this.httpService.getIoTEntities(this.triggerServices).subscribe(
          res => {
            if(res.length > 0){
              this.ruleService.setTriggerEntities(res);
              this.connect = false;
              for(let service of this.triggerServices){
                this.httpService.getTriggers(service.id, this.type).subscribe(res => {
                  for(let tr of res) 
                    this.triggers.push(tr);
                }, error => {
                  this._notificationsService.error("OPS, something goes wrong!", "", "yes");
                });
              }

            }
          }, 
          error => {
          });*/

      }
      );
    }
  }

  goBack(): void {
    this.location.back();
  }

  selectTrigger(trigger){
    this.ruleService.setTrigger(trigger);
    this.router.navigate(['composition/'+ this.type + '/if/details/']);
  }

  connectEntity(serviceId){

    let entity = new IoTEntity();
    entity.name = this.entityName;
    entity.services = [];
    for(let service of this.triggerServices){
      entity.services.push(service.url);
    }
    this.httpService.saveEntity(entity).subscribe(res => {
      this._notificationsService.success("Entity saved", "", "yes");
      window.location.reload();
    }, error => {
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");
    });
  }
}
