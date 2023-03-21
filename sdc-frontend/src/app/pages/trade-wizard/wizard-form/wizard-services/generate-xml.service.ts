import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {FormGroup} from "@angular/forms";
import {TradeDescriptor} from "./trade-descriptor";
import {XmlResponse} from "./xml-response"

@Injectable({
  providedIn: 'root'
})
export class GenerateXmlService {

  private xmlServiceURL: string;

  constructor(private http: HttpClient) {
    this.xmlServiceURL = 'http://localhost:8080/generatexml'
  }

  public generateXml(formJoin: FormGroup): Observable<XmlResponse> {
    let tradeDescriptor: TradeDescriptor;
    tradeDescriptor = new TradeDescriptor(
      formJoin.get('firstCounterparty')!.value,
      formJoin.get('secondCounterparty')!.value,
      formJoin.get('marginBufferAmount')!.value,
      formJoin.get('terminationFeeAmount')!.value,
      formJoin.get('notionalAmount')!.value,
      formJoin.get('currency')!.value,
      formJoin.get('tradeDate')!.value,
      formJoin.get('effectiveDate')!.value,
      formJoin.get('terminationDate')!.value,
      formJoin.get('fixedPayingParty')!.value,
      formJoin.get('fixedRate')!.value,
      formJoin.get('fixedDayCountFraction')!.value,
      formJoin.get('floatingPayingParty')!.value,
      formJoin.get('floatingRateIndex')!.value,
      formJoin.get('floatingDayCountFraction')!.value,
      formJoin.get('floatingFixingDayOffset')!.value,
      formJoin.get('floatingPaymentFrequency')!.value
    );
    return this.http.post<XmlResponse>(this.xmlServiceURL, tradeDescriptor);
  }

}
