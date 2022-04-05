import { Component, OnInit } from '@angular/core';
import { User } from '../user';
import { Router } from '@angular/router';
import { HttpService } from '../http.service';
import { NotificationsService } from 'angular2-notifications';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  user : User;

  constructor(private router: Router, private httpService : HttpService, private _notificationsService: NotificationsService, private authService : AuthService) { }

  ngOnInit() {
    if(this.authService.getAuthStatus() == true)
      this.router.navigate(['home']);
    this.user = new User();
  }

  login(){

    console.log("login...");
    this.httpService.login(this.user).subscribe(res => {

      this.authService.setAuthStatus(true, this.user);
      this._notificationsService.success("You are now authenticated!", "", "yes");
      this.router.navigate(['/home']);
    }, error => {

      this._notificationsService.error("Authentication failed", "", "yes");
    });
  }



}
