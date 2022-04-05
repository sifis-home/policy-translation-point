import { Injectable } from '@angular/core';
import { User } from './User'
import { Observable } from 'rxjs';
import { Observer } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';
import { IoTEntity } from './iotentity';
import { Service } from './service';
import { Trigger } from './trigger';
import { Detail } from './detail';
import { Action } from './action';
import { Rule } from './rule';
@Injectable()
export class HttpService {

  server : string;
  constructor(private _http: HttpClient, private authService : AuthService) {
    this.server = "http://localhost:8080";
    //this.server = "http://toce.polito.it:8080/eud";
  }

  signUp(user:User) {
    let headers = new HttpHeaders();
    headers.append('Content-Type', 'application/json');
    headers.append('Accept', 'application/json');
    //let options = new RequestOptions({ headers: headers });
    return this._http.post(this.server  + "/registration", user, { headers:headers });
  }

  login(user:User) {
    //let headers = new Headers();
    //headers.append('Authorization', "Basic " + btoa(user.username + ":" + user.password));
    //let options = new RequestOptions({ headers : headers });
    //return this._http.get(this.server  + "/login", headers).map((response: Response) => response.json());
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get(this.server  + "/user", {headers: headers});
  }

  

  logout(){
    //let headers = new Headers();
    //headers.append('Authorization', "Basic " + btoa(user.username + ":" + user.password));
    //headers.append('Content-Type', 'text/plain');
    //var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});

    //let options = new RequestOptions({ headers: headers });
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password), 'responseType': 'text'});
    return this._http.post(this.server + "/logout", null, {headers: headers});
  }

  getAllServices(): Observable<Service[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Service[]>(this.server  + "/service", {headers: headers});
  }

  getTriggerServices(type): Observable<Service[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Service[]>(this.server  + "/" + type + "/triggerservice", {headers: headers});
  }

  getTriggers(service, type): Observable<Trigger[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Trigger[]>(this.server  + "/" + type + "/triggerservice/" + service + "/triggers", {headers: headers});
  }

  getTriggerDetails(service,trigger, type): Observable<Detail[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Detail[]>(this.server  + "/" + type + "/triggerservice/" + service + "/triggers/" + trigger + "/details", {headers: headers});
  }

  getActionServices(triggerId, type): Observable<Service[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Service[]>(this.server  + "/" + type + "/actionservice?trigger="+triggerId, {headers: headers});
  }

  getActions(service, type): Observable<Action[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Action[]>(this.server  + "/" + type + "/actionservice/" + service + "/actions", {headers: headers});
  }

  getActionDetails(service,action, type): Observable<Detail[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Detail[]>(this.server  + "/" + type + "/actionservice/" + service + "/actions/" + action + "/details", {headers: headers});
  }

  getIoTEntities(services): Observable<IoTEntity[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.post<IoTEntity[]>(this.server  + "/IoTEntity/list", services, {headers: headers});
  }

  saveEntity(entity){
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.post(this.server  + "/IoTEntity", entity, { headers:headers });
  }

  saveRule(rule, type){
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.post(this.server  + "/" + type + "/rule", rule, { headers:headers });
  }

  checkRule(rule, type){
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.post(this.server  + "/" + type + "/check", rule, { headers:headers });
  }

  deleteRule(ruleId, type){
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.delete(this.server  + "/" + type + "/rule/" + ruleId, { headers:headers });
  }

  translateRule(rule, type){
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.post(this.server  + "/" + type + "/translate", rule, { headers:headers, responseType: 'blob'});
    
  }

  getRules(type): Observable<Rule[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Rule[]>(this.server  + "/" + type + "/rule", {headers: headers});
  }

  selectRules(type,rules){
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.post(this.server  + "/" + type + "/rule/recommendable", rules, { headers:headers });
  }

  getDefinedTriggers(type): Observable<Trigger[]>{
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password)});
    return this._http.get<Trigger[]>(this.server  + "/" + type + "/rule/trigger", {headers: headers});
  }

  getNetwork(){
    let user = this.authService.getAuthUser();
    var headers = new HttpHeaders({'authorization': 'Basic ' + btoa(user.username + ':' + user.password), 'responseType': 'text'});
    return this._http.get(this.server  + "/euddebug/petrinet/build", {headers: headers});
  }
}
