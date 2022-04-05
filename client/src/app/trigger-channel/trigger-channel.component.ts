import { Component, OnInit, AfterViewInit, AfterViewChecked } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { TextFilterPipe } from '../text-filter.pipe';
import { HttpService } from '../http.service';
import { RuleService } from '../rule.service'
import { NotificationsService } from 'angular2-notifications';
import { Service } from '../service';
import { User } from '../user';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-trigger-channel',
  templateUrl: './trigger-channel.component.html',
  styleUrls: ['./trigger-channel.component.css']
})
export class TriggerChannelComponent implements OnInit {
  triggerServices: Service[];
  triggerServicesMap;
  type: string;
  user: User;
  searchTrigger: string;

  constructor(private router: Router, private ruleService :  RuleService, private location: Location, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService) {

  }

  ngOnInit() {

    this.triggerServicesMap = {};
    this.user = this.authService.getAuthUser();

    if (!this.user)
      this.router.navigate(['login']);
    else{

      this.triggerServices = [];


      this.route.params.subscribe(params => {
         this.type = params['type'];
         this.httpService.getTriggerServices(this.type).subscribe(res => {
           for(let service of res){
             if(!this.triggerServicesMap[service.webId]){
               this.triggerServicesMap[service.webId] = [];
               this.triggerServices.push(service);
             }
            this.triggerServicesMap[service.webId].push(service);

           }
         }, error => {

           this._notificationsService.error("OPS, something goes wrong!", "", "yes");
         });
      });
    }
  }

  selectService(service): void{
    this.ruleService.setTriggerServices(this.triggerServicesMap[service.webId]);
    this.router.navigate(['composition/'+ this.type + '/if/trigger']);
  }

  goBack(): void {
    this.location.back();
  }

}
