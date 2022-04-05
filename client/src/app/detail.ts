export class Detail {

  url:string;
  id:string;
  name:string;
  type:string;
  value:any;
  alternatives:any[];

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }
}
