import { Component } from '@angular/core';
import { User } from './user';
import { Router, NavigationEnd } from '@angular/router';
import { Rule } from './rule';
import { RuleService } from './rule.service';
import { HttpService } from './http.service';
import { AuthService } from './auth.service';
import { RecommendationsService } from './recommendations.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  public options = {
        position: ["bottom", "right"],
        timeOut: 5000,
        animate: "fromLeft",
        showProgressBar: true,
        pauseOnHover: false,
        clickToClose: false,
        lastOnBottom: true,
    }


  user: User;
  clicked: boolean;
  type: string;
 

  constructor(private router: Router, private httpService : HttpService, private authService : AuthService, private ruleService: RuleService, private recommendationsService: RecommendationsService) {
    //this.clicked = false;
    this.user = this.authService.getAuthUser();
    this.authService.authenticatedChange.subscribe((data) => {
      this.user = this.authService.getAuthUser();
    });
  }

  logout(): void{
    this.httpService.logout().subscribe(res => {
      console.log(res);
      sessionStorage.clear();
      localStorage.clear();
      //this.router.navigate(['/login']);
      window.location.reload();

    }, error => {
      console.log(error)
    });
  }

  newRule(){
    this.ruleService.clear();
  }
}
