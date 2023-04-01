import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {FormGroup} from "@angular/forms";
import {TradeDescriptor} from "./trade-descriptor";
import {XmlResponse} from "./xml-response"

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
    'Authorization': 'Basic ' + btoa('user1:password1')
  })
};

@Injectable({
  providedIn: 'root'
})
export class GenerateXmlService {

  private xmlServiceURL: string;

  constructor(private http: HttpClient) {
    this.xmlServiceURL = 'http://localhost:8080/generatexml'
  }

  interpretAsUTC(date: Date){
    var timeZoneDifference = (date.getTimezoneOffset() / 60) * -1; //convert to positive value.
    date.setTime(date.getTime() + (timeZoneDifference * 60) * 60 * 1000);
    date.toISOString();
    return date;
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
      this.interpretAsUTC(new Date(formJoin.get('tradeDate')!.value)),
      this.interpretAsUTC(new Date(formJoin.get('effectiveDate')!.value)),
      this.interpretAsUTC(new Date(formJoin.get('terminationDate')!.value)),
      formJoin.get('fixedPayingParty')!.value,
      formJoin.get('fixedRate')!.value,
      formJoin.get('fixedDayCountFraction')!.value,
      formJoin.get('floatingPayingParty')!.value,
      formJoin.get('floatingRateIndex')!.value,
      formJoin.get('floatingDayCountFraction')!.value,
      formJoin.get('floatingFixingDayOffset')!.value,
      formJoin.get('floatingPaymentFrequency')!.value
    );
    return this.http.post<XmlResponse>(this.xmlServiceURL, tradeDescriptor, httpOptions);
  }

}
