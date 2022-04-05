import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { RuleCompositionComponent } from './rule-composition/rule-composition.component';
import { TriggerChannelComponent } from './trigger-channel/trigger-channel.component';
import { RuleService } from './rule.service';
import { RecommendationsService } from './recommendations.service';
import { TriggerSelectionComponent } from './trigger-selection/trigger-selection.component';
import { TriggerDetailsComponent } from './trigger-details/trigger-details.component';
import { RuleStepComponent } from './rule-step/rule-step.component';
import { ActionChannelComponent } from './action-channel/action-channel.component';
import { ActionSelectionComponent } from './action-selection/action-selection.component';
import { ActionDetailsComponent } from './action-details/action-details.component';
import { RuleFinishComponent } from './rule-finish/rule-finish.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { TextFilterPipe } from './text-filter.pipe';
import { RegistrationComponent } from './registration/registration.component';
import { HttpService} from './http.service';
import { AuthService } from './auth.service';
import { SimpleNotificationsModule} from 'angular2-notifications';
import { RuleListComponent } from './rule-list/rule-list.component';
import { RuleProblemsComponent } from './rule-problems/rule-problems.component';
import { ProblemExplanationComponent } from './problem-explanation/problem-explanation.component';
import { ProblemService } from './problem.service';

@NgModule({
  declarations: [
    AppComponent,
    RuleCompositionComponent,
    TriggerChannelComponent,
    TriggerSelectionComponent,
    TriggerDetailsComponent,
    RuleStepComponent,
    ActionChannelComponent,
    ActionSelectionComponent,
    ActionDetailsComponent,
    RuleFinishComponent,
    LoginComponent,
    HomeComponent,
    TextFilterPipe,
    RegistrationComponent,
    RuleListComponent,
    RuleProblemsComponent,
    ProblemExplanationComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    HttpClientXsrfModule.withOptions({
      cookieName: 'XSRF-TOKEN',
      headerName: 'X-XSRF-TOKEN',
    }),
    FormsModule,
    SimpleNotificationsModule.forRoot(),
    RouterModule.forRoot([
     { path: '', redirectTo: '/home', pathMatch: 'full' },
     {
       path: 'composition/:type/list',
       component: RuleListComponent
     },
     {
       path: 'composition/:type/problems',
       component: RuleProblemsComponent
     },
     {
       path: 'composition/:type/problems/explanation',
       component: ProblemExplanationComponent
     },
     {
       path: 'composition/:type',
       component: RuleCompositionComponent
     },
     {
       path: 'composition/:type/if',
       component: TriggerChannelComponent
     },
     {
       path: 'composition/:type/if/trigger',
       component: TriggerSelectionComponent
     },
     {
       path: 'composition/:type/if/details',
       component: TriggerDetailsComponent
     },
     {
       path: 'composition/:type/if/check',
       component: RuleStepComponent
     },
     {
       path: 'composition/:type/then',
       component: ActionChannelComponent
     },
     {
       path: 'composition/:type/then/action',
       component: ActionSelectionComponent
     },
     {
       path: 'composition/:type/then/details',
       component: ActionDetailsComponent
     },
     {
       path: 'composition/:type/then/check',
       component: RuleFinishComponent
     },
     {
       path: 'login',
       component: LoginComponent
     },
     {
       path: 'home',
       component: HomeComponent
     },
     {
       path: 'registration',
       component: RegistrationComponent
     }
   ]),
   CommonModule
  ],
  providers: [HttpService, AuthService, RuleService, ProblemService, RecommendationsService],
  bootstrap: [AppComponent]
})
export class AppModule { }
