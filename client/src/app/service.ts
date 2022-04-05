import { Trigger } from './trigger';
import { Action } from './action';
export class Service {
  url:string;
  id:string;
  name:string;
  color:string;
  image:string;
  webId:string;
  triggers:Trigger[];
  actions:Action[];

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }
}
