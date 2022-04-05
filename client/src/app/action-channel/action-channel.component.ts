import { Component, OnInit, AfterViewInit } from '@angular/core';
import { RuleService } from '../rule.service'
import { NotificationsService } from 'angular2-notifications';
import { HttpService } from '../http.service';
import { Detail } from '../detail';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { User } from '../user';
import { Service } from '../service';
import { TextFilterPipe } from '../text-filter.pipe';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-action-channel',
  templateUrl: './action-channel.component.html',
  styleUrls: ['./action-channel.component.css']
})
export class ActionChannelComponent implements OnInit {

  actionServices: Service[];
  triggerService: Service;
  actionServicesMap;
  type : string;
  user: User;
  searchAction: string;

  constructor(private router: Router, private ruleService :  RuleService, private location: Location, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService){}

  ngOnInit() {
    this.actionServicesMap = {};
    this.user = this.authService.getAuthUser();
    if (!this.user)
      this.router.navigate(['login']);
    else{

      this.actionServices = [];
      this.triggerService = this.ruleService.getTrigger().service;

      this.route.params.subscribe(params => {
         this.type = params['type'];
         this.httpService.getActionServices(this.triggerService.webId, this.type).subscribe(res => {
           for(let service of res){
             if(!this.actionServicesMap[service.webId]){
               this.actionServicesMap[service.webId] = [];
               this.actionServices.push(service);
             }
            this.actionServicesMap[service.webId].push(service);

           }
         }, error => {
           this._notificationsService.error("OPS, something goes wrong!", "", "yes");
         });
      });
    }
  }

  selectService(service): void{
    this.ruleService.setActionServices(this.actionServicesMap[service.webId]);
    this.router.navigate(['composition/'+ this.type + '/then/action']);
  }

  goBack(): void {
    this.location.back();
  }


}
