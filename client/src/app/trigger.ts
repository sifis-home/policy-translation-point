import { Detail } from './detail';
import { Service } from './service';
export class Trigger {
  url:string;
  id:string;
  description:string;
  name:string;
  service: Service;
  entity:string;
  details:Detail[];

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }
}
