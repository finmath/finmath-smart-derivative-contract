import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { counterparties } from "./wizard-form-data/counterparties";
import { currencies } from "./wizard-form-data/currencies"
import { dayCountFractions} from "./wizard-form-data/day-count-fractions";
import { fixingDayOffsets} from "./wizard-form-data/fixing-day-offsets";
import { paymentFrequencies} from "./wizard-form-data/payment-frequencies";
import { GenerateXmlService } from "./wizard-services/generate-xml.service";
import { PricingRequestService } from "./wizard-services/pricing-request.service";
import { WizardPopupComponent} from "./wizard-popup/wizard-popup/wizard-popup.component";
import {TradeDescriptor} from "./wizard-services/trade-descriptor";
import {MatDialog} from "@angular/material/dialog";
import { DefaultService } from '../../../openapi/api/default.service'
import { Counterparty } from '../../../openapi/model/counterparty'
import { SdcXmlRequest } from '../../../openapi/model/sdcXmlRequest'
import { SdcXmlResponse } from '../../../openapi/model/sdcXmlResponse'
import {HttpClient, HttpHeaders} from "@angular/common/http";

export interface DialogData{
  tradeDescription: TradeDescriptor;
  dialogMessage: string;
}

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
    'Authorization': 'Basic ' + btoa('user1:password1')
  })
};

@Component({
  selector: 'app-wizard-form',
  templateUrl: './wizard-form.component.html',
  styleUrls: ['./wizard-form.component.css']
})

export class WizardFormComponent implements OnInit{
  isLinear = false;
  wizardFormFirstStep : FormGroup;
  wizardFormSecondStep: FormGroup;
  wizardFormThirdStep: FormGroup;
  wizardFormFourthStep: FormGroup;
  currencyPrefix: string;
  counterparties = counterparties;
  currencies = currencies;
  dayCountFractions = dayCountFractions;
  paymentFrequencies=paymentFrequencies;
  fixingDayOffsets=fixingDayOffsets;
  tradeDescription: TradeDescriptor | undefined;
  dialogMessage: string | undefined;
  selectedParties = [] as Counterparty[];
  firstPartySelected = false;
  secondPartySelected = false;


  constructor(private readonly defaultService: DefaultService, public dialog: MatDialog, private generateXmlService: GenerateXmlService, private pricingRequestService: PricingRequestService, private _formBuilder: FormBuilder) {
    this.wizardFormFirstStep = this._formBuilder.group({});
    this.wizardFormSecondStep = this._formBuilder.group({});
    this.wizardFormThirdStep = this._formBuilder.group({});
    this.wizardFormFourthStep = this._formBuilder.group({});
    this.currencyPrefix='€';


  }

    interpretAsUTC(date: Date){
      var timeZoneDifference = (date.getTimezoneOffset() / 60) * -1; //convert to positive value.
      date.setTime(date.getTime() + (timeZoneDifference * 60) * 60 * 1000);
      date.toISOString();
      return date;
    }

  ngOnInit(){
    const currencyDefault = this.currencies.find((c :any) => c.code === 'EUR');
    const fixedDayCountFractionDefault = this.dayCountFractions.find((c :any) => c.id === '30E/360');
    const floatingDayCountFractionDefault = this.dayCountFractions.find((c :any) => c.id === 'ACT/360');
    const floatingFixingDayOffsetDefault = this.fixingDayOffsets.find((c :any) => c.id === '-2');

    this.defaultService.defaultHeaders = new HttpHeaders({
                                             'Content-Type':  'application/json',
                                             'Authorization': 'Basic ' + btoa('user1:password1')
                                           });


    this.wizardFormFirstStep = this._formBuilder.group({
      firstCounterparty: ['', Validators.required],
      secondCounterparty: ['', Validators.required],
      marginBufferAmount: [0, [Validators.required, Validators.min(0)]],
      terminationFeeAmount: [0, [Validators.required, Validators.min(0)]],
    });
    this.wizardFormSecondStep = this._formBuilder.group({
      notionalAmount: [0, Validators.min(0)],
      currency: ['', Validators.required],
      tradeDate: ['', Validators.required],
      effectiveDate: ['', Validators.required],
      terminationDate: ['', Validators.required],
    });
    this.wizardFormThirdStep = this._formBuilder.group({
      fixedPayingParty: [{value: '', disabled: true}, Validators.required],
      fixedRate: ['', Validators.required],
      fixedDayCountFraction: ['', Validators.required],
    });
    this.wizardFormFourthStep = this._formBuilder.group({
      floatingPayingParty: [{value: '', disabled: true}, Validators.required],
      floatingRateIndex: ['', Validators.required],
      floatingDayCountFraction: ['', Validators.required],
      floatingFixingDayOffset: ['', Validators.required],
      floatingPaymentFrequency: ['', Validators.required],
    });

    this.wizardFormSecondStep.get('currency')!.setValue(currencyDefault.code);
    this.wizardFormThirdStep.get('fixedDayCountFraction')!.setValue(fixedDayCountFractionDefault.id);
    this.wizardFormFourthStep.get('floatingDayCountFraction')!.setValue(floatingDayCountFractionDefault.id);
    this.wizardFormFourthStep.get('floatingFixingDayOffset')!.setValue(floatingFixingDayOffsetDefault.id);
  }

  pushXMLGenerationRequest(){
    var sdcXmlRequest = {
        firstCounterparty: this.counterparties.find(cp => cp.bicCode === this.wizardFormFirstStep.get('firstCounterparty')!.value),
        secondCounterparty: this.counterparties.find(cp => cp.bicCode === this.wizardFormFirstStep.get('secondCounterparty')!.value),
        marginBufferAmount: this.wizardFormFirstStep.get('marginBufferAmount')!.value,
        terminationFeeAmount: this.wizardFormFirstStep.get('terminationFeeAmount')!.value,
        notionalAmount: this.wizardFormSecondStep.get('notionalAmount')!.value,
        currency: this.wizardFormSecondStep.get('currency')!.value,
        tradeDate: this.interpretAsUTC(new Date(this.wizardFormSecondStep.get('tradeDate')!.value)).toISOString(),
        effectiveDate: this.interpretAsUTC(new Date(this.wizardFormSecondStep.get('effectiveDate')!.value)).toISOString(),
        terminationDate: this.interpretAsUTC(new Date(this.wizardFormSecondStep.get('terminationDate')!.value)).toISOString(),
        fixedPayingParty: this.counterparties.find(cp => cp.bicCode === this.wizardFormThirdStep.get('fixedPayingParty')!.value),
        fixedRate: this.wizardFormThirdStep.get('fixedRate')!.value,
        fixedDayCountFraction: this.wizardFormThirdStep.get('fixedDayCountFraction')!.value,
        floatingPayingParty: this.counterparties.find(cp => cp.bicCode === this.wizardFormFourthStep.get('floatingPayingParty')!.value),
        floatingRateIndex: this.wizardFormFourthStep.get('floatingRateIndex')!.value,
        floatingDayCountFraction: this.wizardFormFourthStep.get('floatingDayCountFraction')!.value,
        floatingFixingDayOffset: this.wizardFormFourthStep.get('floatingFixingDayOffset')!.value,
        floatingPaymentFrequency: this.wizardFormFourthStep.get('floatingPaymentFrequency')!.value
    } as SdcXmlRequest;


    this.defaultService.generateXml(sdcXmlRequest).subscribe(r => {
                                                   this.dialogMessage = r.xmlBody;
                                                   this.openDialog();
                                                 });
  }

  pushPricingRequest(){
    var sdcXmlRequest = {
            firstCounterparty: this.counterparties.find(cp => cp.bicCode === this.wizardFormFirstStep.get('firstCounterparty')!.value),
            secondCounterparty: this.counterparties.find(cp => cp.bicCode === this.wizardFormFirstStep.get('secondCounterparty')!.value),
            marginBufferAmount: this.wizardFormFirstStep.get('marginBufferAmount')!.value,
            terminationFeeAmount: this.wizardFormFirstStep.get('terminationFeeAmount')!.value,
            notionalAmount: this.wizardFormSecondStep.get('notionalAmount')!.value,
            currency: this.wizardFormSecondStep.get('currency')!.value,
            tradeDate: this.interpretAsUTC(new Date(this.wizardFormSecondStep.get('tradeDate')!.value)).toISOString(),
            effectiveDate: this.interpretAsUTC(new Date(this.wizardFormSecondStep.get('effectiveDate')!.value)).toISOString(),
            terminationDate: this.interpretAsUTC(new Date(this.wizardFormSecondStep.get('terminationDate')!.value)).toISOString(),
            fixedPayingParty: this.counterparties.find(cp => cp.bicCode === this.wizardFormThirdStep.get('fixedPayingParty')!.value),
            fixedRate: this.wizardFormThirdStep.get('fixedRate')!.value,
            fixedDayCountFraction: this.wizardFormThirdStep.get('fixedDayCountFraction')!.value,
            floatingPayingParty: this.counterparties.find(cp => cp.bicCode === this.wizardFormFourthStep.get('floatingPayingParty')!.value),
            floatingRateIndex: this.wizardFormFourthStep.get('floatingRateIndex')!.value,
            floatingDayCountFraction: this.wizardFormFourthStep.get('floatingDayCountFraction')!.value,
            floatingFixingDayOffset: this.wizardFormFourthStep.get('floatingFixingDayOffset')!.value,
            floatingPaymentFrequency: this.wizardFormFourthStep.get('floatingPaymentFrequency')!.value
        } as SdcXmlRequest;


    this.defaultService.evaluateFromEditor(sdcXmlRequest).subscribe(r => {
                                                   this.dialogMessage = r.value + r.currency+"@"+r.valuationDate;
                                                   this.openDialog();
                                                 });

  }

  pushTradeInceptionRequest(){
    window.alert("This is where I would incept your trade... if only I had a backend.")
  }
  isAllControlsValid(){
    let formArray = [this.wizardFormFirstStep, this.wizardFormSecondStep, this.wizardFormThirdStep, this.wizardFormFourthStep];
    let formJoin = this._formBuilder.group({});
    for(const formGroup of formArray){
      for(const formControl in formGroup.controls){
        formJoin.addControl(formControl, formGroup.get(formControl)!);
      }
    }
    return formJoin.valid;
  }

  onCurrencyChange(){
    let currencyCode = this.wizardFormSecondStep.get('currency')!.value;
    this.currencyPrefix = this.currencies.find((c :any) => c.code === currencyCode).symbol;
  }

  onFirstPartySelection(){
    if(!this.firstPartySelected) {
      this.selectedParties.push(counterparties.find((p: any) => p.bicCode === this.wizardFormFirstStep.get('firstCounterparty')!.value)!);
      this.firstPartySelected = true;
    }

    if(this.selectedParties.length == 2){
      this.selectedParties[0] = counterparties.find((p: any) => p.bicCode === this.wizardFormFirstStep.get('firstCounterparty')!.value)!;
      console.log("enabling form");
      this.wizardFormThirdStep.get('fixedPayingParty')!.enable();
      this.wizardFormFourthStep.get('floatingPayingParty')!.enable();
    }
    console.log(this.selectedParties.length);
  }

  onSecondPartySelection(){
    if(!this.secondPartySelected) {
      this.selectedParties.push(counterparties.find((p: any) => p.bicCode === this.wizardFormFirstStep.get('secondCounterparty')!.value)!);
      this.secondPartySelected = true;
    }

    if(this.selectedParties.length == 2){
      this.selectedParties[1] = counterparties.find((p: any) => p.bicCode === this.wizardFormFirstStep.get('secondCounterparty')!.value)!;
      this.wizardFormThirdStep.get('fixedPayingParty')!.enable();
      this.wizardFormFourthStep.get('floatingPayingParty')!.enable();
    }

    console.log(this.selectedParties.length);

  }

  onPayerPartySelection(){
    if((this.wizardFormThirdStep.get('fixedPayingParty')!.value === this.wizardFormFourthStep.get('floatingPayingParty')!.value)
    && (this.wizardFormThirdStep.get('fixedPayingParty')!.dirty)
    && (this.wizardFormFourthStep.get('floatingPayingParty')!.dirty)){ //do not report errors unless the user actually tried to do the wrong thing
      this.wizardFormThirdStep.get('fixedPayingParty')!.setErrors({incorrect: true});
      this.wizardFormFourthStep.get('floatingPayingParty')!.setErrors({incorrect: true});
      this.wizardFormThirdStep.updateValueAndValidity();
      this.wizardFormFourthStep.updateValueAndValidity();
    } else {
      this.wizardFormThirdStep.get('fixedPayingParty')!.setErrors(null);
      this.wizardFormFourthStep.get('floatingPayingParty')!.setErrors(null);
      this.wizardFormThirdStep.updateValueAndValidity();
      this.wizardFormFourthStep.updateValueAndValidity();
    }
  }



  openDialog(): void {
    const dialogRef = this.dialog.open(WizardPopupComponent, {
      data: {tradeDescription: this.tradeDescription, dialogMessage: this.dialogMessage},
      width: '50%',
      height: '50%',
    });

    dialogRef.afterClosed().subscribe(result => {

    });
  }
}
