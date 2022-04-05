export class IoTEntity {
  url: string;
  id: string;
  type: string;
  name: string;
  services: string[];

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }

}
