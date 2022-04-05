export class User {
  id: number;
  username: string;
  password: string;
  name: string;
  lastName: string;
  ontologyIRI: string;
  active: number;

  constructor(values: Object = {}) {
    Object.assign(this, values);
  }
}
