import { Component, OnInit } from '@angular/core';
import { HttpService } from '../http.service'
import { User } from '../user'
import { Router } from '@angular/router';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  user : User;

  constructor(private router: Router, private httpService : HttpService, private _notificationsService: NotificationsService) { }

  ngOnInit() {
    this.user = new User();

  }

  signUp(){

    this.httpService.signUp(this.user).subscribe(res => {

      this._notificationsService.success("You are now registered!", "", "yes");
      this.router.navigate(['/login']);
    }, error => {

      this._notificationsService.error("Registration failed", "", "yes");
    });
  }

}
