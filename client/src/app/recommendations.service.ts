import { Injectable } from '@angular/core';
import { Rule } from './rule';
import { Subject } from 'rxjs' ;

@Injectable()
export class RecommendationsService {

  recommendedRules: Rule[];
  private recommendedRulesExp = new Subject<Rule[]>();
  public recommendedRulesExp$ = this.recommendedRulesExp.asObservable();

  constructor() {
    this.recommendedRules = [];
  }

  setRecommendations(recommendations){
    this.recommendedRules = recommendations;
    this.recommendedRulesExp.next(recommendations);
  }

  getRecommendations(){
    return this.recommendedRules;
  }

}
