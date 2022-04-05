import { Detail } from './detail'
import { Service } from './service'

export class Action {
  url:string;
  id:string;
  name:string;
  description:string;
  service:Service;
  entity:string;
  details:Detail[];

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }

}
