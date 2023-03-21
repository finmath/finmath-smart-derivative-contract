import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { counterparties, Counterparty } from "./wizard-form-data/counterparties";
import { currencies } from "./wizard-form-data/currencies"
import { dayCountFractions} from "./wizard-form-data/day-count-fractions";
import { fixingDayOffsets} from "./wizard-form-data/fixing-day-offsets";
import { paymentFrequencies} from "./wizard-form-data/payment-frequencies";
import { GenerateXmlService } from "./wizard-services/generate-xml.service";
import { WizardPopupComponent} from "./wizard-popup/wizard-popup/wizard-popup.component";
import {TradeDescriptor} from "./wizard-services/trade-descriptor";
import {MatDialog} from "@angular/material/dialog";

export interface DialogData{
  tradeDescription: TradeDescriptor;
  dialogMessage: string;
}

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


  constructor(public dialog: MatDialog, private generateXmlService: GenerateXmlService, private _formBuilder: FormBuilder) {
    this.wizardFormFirstStep = this._formBuilder.group({});
    this.wizardFormSecondStep = this._formBuilder.group({});
    this.wizardFormThirdStep = this._formBuilder.group({});
    this.wizardFormFourthStep = this._formBuilder.group({});
    this.currencyPrefix='€';


  }

  ngOnInit(){
    const currencyDefault = this.currencies.find((c :any) => c.code === 'EUR');
    const fixedDayCountFractionDefault = this.dayCountFractions.find((c :any) => c.id === '30E/360');
    const floatingDayCountFractionDefault = this.dayCountFractions.find((c :any) => c.id === 'ACT/360');
    const floatingFixingDayOffsetDefault = this.fixingDayOffsets.find((c :any) => c.id === '-2');


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
    let formArray = [this.wizardFormFirstStep, this.wizardFormSecondStep, this.wizardFormThirdStep, this.wizardFormFourthStep];
    let formJoin = this._formBuilder.group({});
    for(const formGroup of formArray){
      for(const formControl in formGroup.controls){
        formJoin.addControl(formControl, formGroup.get(formControl)!);
      }
    }

    this.generateXmlService.generateXml(formJoin).subscribe(r => {
      this.dialogMessage = r.xmlBody;
      this.openDialog();
    });

  }

  pushPricingRequest(){
    window.alert("This is where I would price your contract... if only I had a backend.")
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
      this.selectedParties.push(counterparties.find((p: any) => p.fullName === this.wizardFormFirstStep.get('firstCounterparty')!.value)!);
      this.firstPartySelected = true;
    }

    if(this.selectedParties.length == 2){
      this.selectedParties[0] = counterparties.find((p: any) => p.fullName === this.wizardFormFirstStep.get('firstCounterparty')!.value)!;
      console.log("enabling form");
      this.wizardFormThirdStep.get('fixedPayingParty')!.enable();
      this.wizardFormFourthStep.get('floatingPayingParty')!.enable();
    }
    console.log(this.selectedParties.length);
  }

  onSecondPartySelection(){
    if(!this.secondPartySelected) {
      this.selectedParties.push(counterparties.find((p: any) => p.fullName === this.wizardFormFirstStep.get('secondCounterparty')!.value)!);
      this.secondPartySelected = true;
    }

    if(this.selectedParties.length == 2){
      this.selectedParties[1] = counterparties.find((p: any) => p.fullName === this.wizardFormFirstStep.get('secondCounterparty')!.value)!;
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
