import { Counterparty } from './../openapi/model/counterparty';
import { Pipe, PipeTransform } from "@angular/core";

/**
 * Class containing the excludeFilter pipe.
 */
@Pipe({
  name: "excludeFilter",
  pure: false,
})
export class ExcludeCounterpartyFilterPipe implements PipeTransform {
  /**
 * Pipe used to implement a filter that excludes counterparties 
 * matching a certain BIC code from a list of counterparties.
 * 
 * @usageNotes
 * ### You may use this filter in combination with an *ngFor directive as in
 * ´´´
 * let counterparty of counterparties | excludeFilter : bicCodeToBeExcluded
 * ´´´
 * 
 * @param{string} filter: the exclusion filter
 * @param{Counterparty[]} items: the list to be filtered out
 */
  transform(items: Counterparty[], filter: string): Counterparty[] {
    if (!items || !filter) {
      return items;
    }
    return items.filter((item) => item.bicCode !== filter);
  }
}
