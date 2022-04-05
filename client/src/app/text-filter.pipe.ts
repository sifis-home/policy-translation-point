import { Pipe, PipeTransform } from '@angular/core';
import { Service } from './service';
@Pipe({
  name: 'textFilter',
  pure: false
})
export class TextFilterPipe implements PipeTransform {

  transform(services, filter) {
    if(!services)
      return null;
    if(!filter)
      return services;
    return services.filter(service => service.name.toLowerCase().indexOf(filter.toLowerCase()) >= 0)

  }

}
