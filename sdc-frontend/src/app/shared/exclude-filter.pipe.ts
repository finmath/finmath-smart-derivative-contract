import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'excludeFilter',
  pure: false
})
export class ExcludeFilterPipe implements PipeTransform {
  transform(items: any[], filter: string): any {
    if (!items || !filter) {
      return items;
    }
    // filter items array, items which match and return true will be
    // kept, false will be filtered out
    return items.filter(item => item.fullName !== filter);
  }
}
