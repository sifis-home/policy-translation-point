import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';
import { NotificationsService } from 'angular2-notifications';
import { HttpService } from '../http.service';
import { RuleService } from '../rule.service'
import { Action } from '../action'
import { Detail } from '../detail';
import { User } from '../user';
import { Service } from '../service';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { IoTEntity } from '../iotentity';

@Component({
  selector: 'app-action-selection',
  templateUrl: './action-selection.component.html',
  styleUrls: ['./action-selection.component.css']
})
export class ActionSelectionComponent implements OnInit {
  actionServices: Service[];
  actionService: Service;
  actions: Action[];
  type: string;
  user: User;
  connect: boolean = false;

  entityName: string;

  constructor(private ruleService : RuleService, private route: ActivatedRoute, private location: Location, private router: Router, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService){}

  ngOnInit() {

    this.connect = true;

    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{

      this.actionServices = this.ruleService.getActionServices();
      this.actionService = this.actionServices[0];
      //this.ruleService.setActionService(this.actionService);

      this.actions = [];

      this.route.params.subscribe(params => {
        this.type = params['type'];

        this.connect = false;
        for(let service of this.actionServices){
          this.httpService.getActions(service.id, this.type).subscribe(res => {
            for(let ac of res)
              this.actions.push(ac);

          }, error => {
            this._notificationsService.error("OPS, something goes wrong!", "", "yes");
          });
        }


        /*this.httpService.getIoTEntities(this.actionServices).subscribe(res => {
          if(res.length > 0){
            this.ruleService.setActionEntities(res);
            this.connect = false;
            for(let service of this.actionServices){
              this.httpService.getActions(service.id, this.type).subscribe(res => {
                for(let ac of res)
                  this.actions.push(ac);

              }, error => {
                this._notificationsService.error("OPS, something goes wrong!", "", "yes");
              });
            }
          }

        }, error => {
          this._notificationsService.error("OPS, something goes wrong!", "", "yes");
        });*/
      }
      );
    }
  }

  goBack(): void {
    this.location.back();
  }

  selectAction(action){
    this.ruleService.setAction(action);
    this.router.navigate(['composition/'+ this.type + '/then/details/']);
  }

  connectEntity(serviceId){

    let entity = new IoTEntity();
    entity.name = this.entityName;
    entity.services = [];
    for(let service of this.actionServices){
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
