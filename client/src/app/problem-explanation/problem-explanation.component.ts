import { Component, OnInit, ViewChild, AfterViewChecked, ElementRef, Input } from '@angular/core';
import { HttpService } from '../http.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { RuleService } from '../rule.service'
import { NotificationsService } from 'angular2-notifications';
import { Service } from '../service';
import { User } from '../user';
import { AuthService } from '../auth.service';
import { Rule } from '../rule';
import { Trigger } from '../trigger';
import { ProblemService } from '../problem.service';


@Component({
  selector: 'app-problem-explanation',
  templateUrl: './problem-explanation.component.html',
  styleUrls: ['./problem-explanation.component.css']
})
export class ProblemExplanationComponent implements OnInit {

  ruleDict;
  problematic;
  implies;
  inprogress: boolean;
  type: string;
  user: User;
  //rules: Rule[];
  simulated: Rule[];
  rules: Rule[];
  temp: Rule[];
  inconsistent: boolean;
  redundant: boolean;
  loop: boolean;
  rule: Rule;
  problematicRules: Rule[];
  startTrigger:Trigger;
  definedRule;

  @ViewChild('scrollMe') private myScrollContainer: ElementRef;

  constructor(private router: Router, private ruleService :  RuleService, private location: Location, private route: ActivatedRoute, private httpService : HttpService,private _notificationsService: NotificationsService, private authService : AuthService, private problemService:ProblemService) { }

  ngOnInit() {
    this.temp = [];
    this.problematic = {};
    this.implies = {};
    this.simulated = [];
    this.ruleDict = {};
    this.inprogress = false;
    this.user = this.authService.getAuthUser();
    this.rules = this.problemService.getRules();
    this.inconsistent = this.problemService.getInconsistent();
    this.redundant = this.problemService.getRedundant();
    this.loop = this.problemService.getLoop();
    this.rule = this.problemService.getRule();
    this.problematicRules = this.problemService.getProblematicRules();
    this.startTrigger = this.problemService.getStartTrigger();


    //this.simulation.scrollIntoView();

    //document.getElementById("ruleSimulation").scrollIntoView(true);
    if (!this.user){
      this.router.navigate(['login']);
    }
    else{

        this.route.params.subscribe(params => {
          this.type = params['type'];
          if(!this.rules || !this.rule || !this.problematicRules || !this.startTrigger)
            this.router.navigate(['composition/'+ this.type + '/problems']);
          for(let rule of this.rules){
            //this.enabled[rule.dbId] = false;
            this.ruleDict[rule.dbId] = rule;
            this.problematic[rule.dbId] = false;
            this.implies[rule.dbId] = false;
          }


        });

    }
    //this.scrollToBottom();
  }

  ngAfterViewChecked() {
    //this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { console.log(err);}
  }


  startSimulation(trigger){

    this.inprogress = true;


    /*if(this.temp.length == 0){
      for(let r of this.problematicRules){
        this.problematic[r.dbId] = true;
      }
      this._notificationsService.info("The simulation is over", "", "yes");
    }*/
    /*this.inprogress = true;
    this.spinnerService.show();
    this.httpService.startSimulation(trigger, this.type).subscribe((res: number) => {
      this.spinnerService.hide();
      if(res == -1){
        //simulation stopped
        for(let rule of this.problematicRules){
          this.problematic[rule.dbId] = true;
        }
        this._notificationsService.info("The simulation is over", "", "yes");
      }
      else{
        //highlight the executed rule
        //this.enabled[res]= true;
        this.simulated.push(this.ruleDict[res]);
      }
    }, error => {
      this.spinnerService.hide();
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");
    });*/
  }

  stepSimulation(){
    this.scrollToBottom();
    if(this.simulated.length == 0){
      if(this.loop){
        this.definedRule = -1;
        this.rules[0].dbId = -1;
        for(let rule of this.rules){
          this.temp.push(rule);
          //this.enabled[rule.dbId] = false;
          //this.ruleDict[rule.dbId] = rule;
          this.problematic[rule.dbId] = false;
        }
        this.temp.reverse();

        this.simulated.push(this.temp.pop());
        if(this.temp.length == 0){
          for(let rule of this.rules){
            this.temp.push(rule);
            //this.enabled[rule.dbId] = false;
            //this.ruleDict[rule.dbId] = rule;
            this.problematic[rule.dbId] = false;
          }
          this.temp.reverse();
        }
      }
      else{

        for(let rule of this.rules){
          this.temp.push(rule);
          //this.enabled[rule.dbId] = false;
          //this.ruleDict[rule.dbId] = rule;
          this.problematic[rule.dbId] = false;
        }

        this.temp.reverse();

        let r = this.temp.pop();
        this.simulated.push(r);

        while(this.temp.length > 0 && this.temp[this.temp.length-1].trigger.id == r.trigger.id){
          this.simulated.push(this.temp.pop());
        }
      }
    }
    else{
      if(this.loop){
        if(this.temp.length == 0){
          for(let rule of this.rules){
            this.temp.push(rule);
            //this.enabled[rule.dbId] = false;
            //this.ruleDict[rule.dbId] = rule;
            this.problematic[rule.dbId] = false;
          }
          this.temp.reverse();
        }

        this.implies[this.simulated[this.simulated.length - 1].dbId] = true;

        this.simulated.push(this.temp.pop());
        this.implies[this.simulated[this.simulated.length - 1].dbId] = true;

      }
      else{
        if(this.temp.length == 0){
          for(let r of this.problematicRules){
            this.problematic[r.dbId] = true;
          }
          this.definedRule = this.rule.dbId;
          this._notificationsService.info("The simulation is over", "", "yes");
        }
        else{
          this.implies[this.simulated[this.simulated.length - 1].dbId] = true;

          let r = this.temp.pop();
          this.simulated.push(r);


          while(this.temp.length > 0 && this.temp[this.temp.length-1].trigger.id == r.trigger.id){
            this.simulated.push(this.temp.pop());
          }
        }
      }
    }


    /*this.spinnerService.show();
    this.httpService.stepSimulation(this.type).subscribe((res: number) => {
      this.spinnerService.hide();
      if(res == -1){
        //simulation stopped
        for(let rule of this.problematicRules){
          this.problematic[rule.dbId] = true;
        }
        this._notificationsService.info("The simulation is over", "", "yes");


      }
      else{

        this.simulated.push(this.ruleDict[res]);
      }
    }, error =>{
      this.spinnerService.hide();
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");
    });*/
  }

  stopSimulation(){
    this.temp = [];
    this.simulated = [];
    this.inprogress = false;
    for(let rule of this.rules){
      //this.enabled[rule.dbId] = false;
      this.ruleDict[rule.dbId] = rule;
      this.problematic[rule.dbId] = false;
      this.implies[rule.dbId] = false;
    }

    /*this.spinnerService.show();
    this.inprogress = false;
    this.httpService.stopSimulation(this.type).subscribe(res => {
      this.simulated.splice(0,this.simulated.length);
      this.spinnerService.hide();
      this._notificationsService.success("Simulation stopped!", "", "yes");
    }, error => {
      this.spinnerService.hide();
      this._notificationsService.error("OPS, something goes wrong!", "", "yes");
    });*/
  }

  closeSimulation(){
    this.router.navigate(['composition/'+ this.type + '/problems']);
  }

  goBack(){
    this.router.navigate(['composition/'+ this.type + '/problems']);
  }

}
