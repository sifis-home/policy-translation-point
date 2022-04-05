import { Injectable } from '@angular/core';
import { User } from './user'
import { Observable } from 'rxjs';
import { Observer } from 'rxjs';

@Injectable()
export class AuthService {
  authenticated: boolean;
  authenticatedUser: User;
  authenticatedChange: Observable<any>;
  authenticatedChangeObserver: Observer<any>;

  constructor() {
    this.authenticatedChange = new Observable<any>(observer => {
      this.authenticatedChangeObserver = observer;
    })
  }

  setAuthStatus(auth: boolean, user: User){
    this.authenticated = auth;
    this.authenticatedUser = user;
    if(user)
      sessionStorage.setItem("user",JSON.stringify(user));
    this.authenticatedChangeObserver.next(this.authenticated);
  } 

  getAuthStatus(){
    let user = this.authenticatedUser;
    if(!user )
      user = JSON.parse(sessionStorage.getItem("user"));

    if(user)
      this.authenticated = true;

    return this.authenticated;
  }

  getAuthUser(){
    let user = this.authenticatedUser;
    if(!user && sessionStorage.getItem("user"))
      user = JSON.parse(sessionStorage.getItem("user"));
    return user;
  }

}
