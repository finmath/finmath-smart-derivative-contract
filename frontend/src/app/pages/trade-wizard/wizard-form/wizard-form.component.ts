import { Component, OnInit } from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from "@angular/forms";
import { counterparties } from "./wizard-form-data/counterparties";
import { Currency, currencies } from "./wizard-form-data/currencies";
import {
  DayCountFraction,
  dayCountFractions,
} from "./wizard-form-data/day-count-fractions";
import {
  FixingDayOffset,
  fixingDayOffsets,
} from "./wizard-form-data/fixing-day-offsets";
import { paymentFrequencies } from "./wizard-form-data/payment-frequencies";
import { WizardPopupComponent } from "./wizard-popup/wizard-popup/wizard-popup.component";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { DefaultService } from "../../../openapi/api/default.service";
import { Counterparty } from "../../../openapi/model/counterparty";
import { SdcXmlRequest } from "../../../openapi/model/sdcXmlRequest";
import { HttpHeaders } from "@angular/common/http";
import { debounceTime } from "rxjs";
import * as moment from "moment";
import { WizardTableDialogComponent } from "./wizard-table-dialog/wizard-table-dialog.component";
import { CashflowPeriod } from "src/app/openapi";

export interface TextDialogData {
  dialogMessage: string;
  dialogWindowTitle: string;
}

const httpOptions = {
  headers: new HttpHeaders({
    "Content-Type": "application/json",
    Authorization: "Basic " + window.btoa("user1:password1"),
  }),
};

@Component({
  selector: "app-wizard-form",
  templateUrl: "./wizard-form.component.html",
  styleUrls: ["./wizard-form.component.scss"],
})
export class WizardFormComponent implements OnInit {
  swapForm: FormGroup;
  currencyPrefix: string;
  counterparties = counterparties;
  currencies = currencies;
  dayCountFractions = dayCountFractions;
  paymentFrequencies = paymentFrequencies;
  fixingDayOffsets = fixingDayOffsets;
  dialogMessage: string | undefined;
  dialogWindowTitle: string | undefined;
  selectedParties = [] as Counterparty[];
  firstPartySelected = false;
  secondPartySelected = false;
  currentNpv = 0;
  terminationDateQuickCommand: FormControl;
  tradeDateQuickCommand: FormControl;
  effectiveDateQuickCommand: FormControl;
  quickCommandRegExp: RegExp =
    /(?<now>^\!$)|(?<notJustNow>^(?<baseDate>[\!tem])?(?<addRemove>[\+\-])(?<dateMsd>\d+[ymd])(?<date2sd>\d+[md])?(?<dateLsd>\d+d)?$)/gm;

  constructor(
    private _snackBar: MatSnackBar,
    private readonly defaultService: DefaultService,
    public dialog: MatDialog,
    private _formBuilder: FormBuilder
  ) {
    this.swapForm = this._formBuilder.group({});
    this.currencyPrefix = "€";
    this.terminationDateQuickCommand = this._formBuilder.control("");
    this.tradeDateQuickCommand = this._formBuilder.control("");
    this.effectiveDateQuickCommand = this._formBuilder.control("");
  }

  interpretAsUTC(date: Date) {
    let timeZoneDifference = (date.getTimezoneOffset() / 60) * -1; //convert to positive value.
    date.setTime(date.getTime() + timeZoneDifference * 60 * 60 * 1000);
    return date;
  }

  ngOnInit() {
    const currencyDefault = this.currencies.find(
      (c: Currency) => c.code === "EUR"
    )!;
    const fixedDayCountFractionDefault = this.dayCountFractions.find(
      (c: DayCountFraction) => c.id === "30E/360"
    )!;
    const floatingDayCountFractionDefault = this.dayCountFractions.find(
      (c: DayCountFraction) => c.id === "ACT/360"
    )!;
    const floatingFixingDayOffsetDefault = this.fixingDayOffsets.find(
      (c: FixingDayOffset) => c.id === "-2"
    )!;

    this.defaultService.defaultHeaders = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Basic " + window.btoa("user1:password1"),
    });

    this.swapForm = this._formBuilder.group({
      firstCounterparty: ["", Validators.required],
      secondCounterparty: ["", Validators.required],
      marginBufferAmount: [0, [Validators.required, Validators.min(0)]],
      terminationFeeAmount: [0, [Validators.required, Validators.min(0)]],
      notionalAmount: [0, Validators.min(0)],
      currency: ["", Validators.required],
      tradeDate: ["", Validators.required],
      effectiveDate: ["", Validators.required],
      terminationDate: ["", Validators.required],
      fixedPayingParty: [{ value: "", disabled: true }, Validators.required],
      fixedRate: ["", Validators.required],
      fixedDayCountFraction: ["", Validators.required],
      fixedPaymentFrequency: ["", Validators.required],
      floatingPayingParty: [{ value: "", disabled: true }, Validators.required],
      floatingRateIndex: ["", Validators.required],
      floatingDayCountFraction: ["", Validators.required],
      floatingFixingDayOffset: ["", Validators.required],
      floatingPaymentFrequency: ["", Validators.required],
      currentNpv: "",
    });

    this.swapForm.get("currency")!.setValue(currencyDefault.code);
    this.swapForm
      .get("fixedDayCountFraction")!
      .setValue(fixedDayCountFractionDefault.id);
    this.swapForm
      .get("floatingDayCountFraction")!
      .setValue(floatingDayCountFractionDefault.id);
    this.swapForm
      .get("floatingFixingDayOffset")!
      .setValue(floatingFixingDayOffsetDefault.id);

    this.swapForm.valueChanges
      .pipe(debounceTime(500))
      .subscribe((selectedValue) => {
        console.log("hi mom");
        if (this.isAllControlsValid()) {
          (
            document
              .getElementsByClassName("currentNpvLabelArea")
              .item(0) as HTMLDivElement
          ).innerHTML = "Current NPV: loading...";
          this.pushPricingRequest();
        }
      });
  }

  mapRequest() {
    return {
      firstCounterparty: this.counterparties.find(
        (cp) => cp.bicCode === this.swapForm.get("firstCounterparty")!.value
      ),
      secondCounterparty: this.counterparties.find(
        (cp) => cp.bicCode === this.swapForm.get("secondCounterparty")!.value
      ),
      marginBufferAmount: this.swapForm.get("marginBufferAmount")!.value,
      terminationFeeAmount: this.swapForm.get("terminationFeeAmount")!.value,
      notionalAmount: this.swapForm.get("notionalAmount")!.value,
      currency: this.swapForm.get("currency")!.value,
      tradeDate: this.interpretAsUTC(
        new Date(this.swapForm.get("tradeDate")!.value)
      ).toISOString(),
      effectiveDate: this.interpretAsUTC(
        new Date(this.swapForm.get("effectiveDate")!.value)
      ).toISOString(),
      terminationDate: this.interpretAsUTC(
        new Date(this.swapForm.get("terminationDate")!.value)
      ).toISOString(),
      fixedPayingParty: this.counterparties.find(
        (cp) => cp.bicCode === this.swapForm.get("fixedPayingParty")!.value
      ),
      fixedRate: this.swapForm.get("fixedRate")!.value,
      fixedDayCountFraction: this.swapForm.get("fixedDayCountFraction")!.value,
      fixedPaymentFrequency: this.paymentFrequencies.find(
        (pf) =>
          pf.fullName === this.swapForm.get("fixedPaymentFrequency")!.value
      ),
      floatingPayingParty: this.counterparties.find(
        (cp) => cp.bicCode === this.swapForm.get("floatingPayingParty")!.value
      ),
      floatingRateIndex: this.swapForm.get("floatingRateIndex")!.value,
      floatingDayCountFraction: this.swapForm.get("floatingDayCountFraction")!
        .value,
      floatingFixingDayOffset: this.swapForm.get("floatingFixingDayOffset")!
        .value,
      floatingPaymentFrequency: this.paymentFrequencies.find(
        (pf) =>
          pf.fullName === this.swapForm.get("floatingPaymentFrequency")!.value
      ),
    } as SdcXmlRequest;
    
  }

  pushXMLGenerationRequest() {
    this.defaultService.generateXml(this.mapRequest()).subscribe({
      next: (sdcXmlResponse) => {
        this.dialogMessage = sdcXmlResponse.xmlBody;
        this.dialogWindowTitle = "Your SDCmL document:";
        this.openTextDialog();
      },
      error: (error) => {
        this._snackBar.open(
          "Oopsies, something went wrong. A developer might want to know about the stuff in the console log.",
          "OK",
          {
            horizontalPosition: "right",
            verticalPosition: "top",
            duration: 7500,
          }
        );
        console.log(JSON.stringify(error));
      },
    });
  }

  pushFixedScheduleGenerationRequest() {
    console.log(this.mapRequest());
    this.defaultService.getFixedSchedule(this.mapRequest()).subscribe({
      next: (cashflowPeriods) => {
        this.dialogMessage = JSON.stringify(cashflowPeriods);
        this.dialogWindowTitle = "Your SDCmL document:";
        this.openTableDialog(cashflowPeriods);
      },
      error: (error) => {
        this._snackBar.open(
          "Oopsies, something went wrong. A developer might want to know about the stuff in the console log.",
          "OK",
          {
            horizontalPosition: "right",
            verticalPosition: "top",
            duration: 7500,
          }
        );
        console.log(JSON.stringify(error));
      },
    });
  }

  pushFloatingScheduleGenerationRequest() {
    console.log(this.mapRequest());
    this.defaultService.getFloatingSchedule(this.mapRequest()).subscribe({
      next: (cashflowPeriods) => {
        this.dialogMessage = JSON.stringify(cashflowPeriods);
        this.dialogWindowTitle = "Your SDCmL document:";
        this.openTableDialog(cashflowPeriods);
      },
      error: (error) => {
        this._snackBar.open(
          "Oopsies, something went wrong. A developer might want to know about the stuff in the console log.",
          "OK",
          {
            horizontalPosition: "right",
            verticalPosition: "top",
            duration: 7500,
          }
        );
        console.log(JSON.stringify(error));
      },
    });
  }

  loadTemplate() {
    window.alert(
      "This is where I would show you your templates... if only I knew how."
    );
  }

  pushPricingRequest() {
    this.defaultService.evaluateFromEditor(this.mapRequest()).subscribe({
      next: (valueResponse) => {
        console.log(JSON.stringify(valueResponse));
        (
          document
            .getElementsByClassName("currentNpvLabelArea")
            .item(0) as HTMLDivElement
        ).innerHTML =
          "Current NPV: " + valueResponse.value + this.currencyPrefix;
      },
      error: (error) => {
        (
          document
            .getElementsByClassName("currentNpvLabelArea")
            .item(0) as HTMLDivElement
        ).innerHTML = "Current NPV: last valuation failed!";
        this._snackBar.open(
          "Oopsies, something went wrong. A developer might want to know about the stuff in the console log.",
          "OK",
          {
            horizontalPosition: "right",
            verticalPosition: "top",
            duration: 15000,
          }
        );
        console.log(JSON.stringify(error));
      },
    });
  }

  pushTradeInceptionRequest() {
    window.alert(
      "This is where I would incept your trade... if only I had a backend."
    );
  }

  isAllControlsValid() {
    return this.swapForm.valid;
  }

  onCurrencyChange() {
    let currencyCode = this.swapForm.get("currency")!.value;
    this.currencyPrefix = this.currencies.find(
      (c: Currency) => c.code === currencyCode
    )!.symbol;
  }

  onFirstPartySelection() {
    if (!this.firstPartySelected) {
      this.selectedParties.push(
        counterparties.find(
          (p: any) =>
            p.bicCode === this.swapForm.get("firstCounterparty")!.value
        )!
      );
      this.firstPartySelected = true;
    }

    if (this.selectedParties.length == 2) {
      this.selectedParties[0] = counterparties.find(
        (p: any) => p.bicCode === this.swapForm.get("firstCounterparty")!.value
      )!;
      console.log("enabling form");
      this.swapForm.get("fixedPayingParty")!.enable();
      this.swapForm.get("floatingPayingParty")!.enable();
    }
    console.log(this.selectedParties.length);
  }

  onSecondPartySelection() {
    if (!this.secondPartySelected) {
      this.selectedParties.push(
        counterparties.find(
          (p: any) =>
            p.bicCode === this.swapForm.get("secondCounterparty")!.value
        )!
      );
      this.secondPartySelected = true;
    }

    if (this.selectedParties.length == 2) {
      this.selectedParties[1] = counterparties.find(
        (p: any) => p.bicCode === this.swapForm.get("secondCounterparty")!.value
      )!;
      this.swapForm.get("fixedPayingParty")!.enable();
      this.swapForm.get("floatingPayingParty")!.enable();
    }

    console.log(this.selectedParties.length);
  }

  onPayerPartySelection() {
    if (
      this.swapForm.get("fixedPayingParty")!.value ===
        this.swapForm.get("floatingPayingParty")!.value &&
      this.swapForm.get("fixedPayingParty")!.dirty &&
      this.swapForm.get("floatingPayingParty")!.dirty
    ) {
      //do not report errors unless the user actually tried to do the wrong thing
      this.swapForm.get("fixedPayingParty")!.setErrors({ incorrect: true });
      this.swapForm.get("floatingPayingParty")!.setErrors({ incorrect: true });
      this.swapForm.updateValueAndValidity();
    } else {
      this.swapForm.get("fixedPayingParty")!.setErrors(null);
      this.swapForm.get("floatingPayingParty")!.setErrors(null);
      this.swapForm.updateValueAndValidity();
    }
  }

  openTextDialog(): void {
    const dialogRef = this.dialog.open(WizardPopupComponent, {
      data: {
        dialogMessage: this.dialogMessage,
        dialogWindowTitle: this.dialogWindowTitle,
      },
      width: "50%",
      height: "50%",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }

  openTableDialog(tableData: CashflowPeriod[]): void {
    const dialogRef = this.dialog.open(WizardTableDialogComponent, {
      data: tableData,
      width: "80%",
      height: "80%",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }

  onQuickCommandChange(
    _targetControl: AbstractControl | null,
    quickCommandControl: FormControl
  ): void {
    let quickCommand = quickCommandControl.value as string;
    if (!_targetControl) {
      console.log("wtf?");
    }
    let targetControl = _targetControl as FormControl;
    let baseDate: Date = new Date();
    let timeDiff: moment.Duration = moment.duration("00:00:00");
    let addOrSubtract: (
      timeDiff: moment.Duration,
      amount: string,
      unit: any
    ) => moment.Duration = (
      timeDiff: moment.Duration,
      amount: string,
      unit: any
    ) => timeDiff.add(amount, unit);

    if (!quickCommand.match(this.quickCommandRegExp)) {
      console.log("not a valid command");
      quickCommandControl.setErrors({ incorrect: true });
      this._snackBar.open(
        "This was not a valid command. Syntax: {! | REF_SELECTOR{+|-}TIME_LENGTH}. If you need more help, see the documentation or ask a dev.",
        "OK",
        {
          horizontalPosition: "right",
          verticalPosition: "top",
          duration: 7500,
        }
      );
      quickCommandControl.reset();
      return;
    }

    for (const match of quickCommand.matchAll(this.quickCommandRegExp)) {
      if (match.groups!["now"]) {
        targetControl.setValue(new Date());
        targetControl.updateValueAndValidity();
        this._snackBar.open("Date set!", "OK", {
          horizontalPosition: "right",
          verticalPosition: "top",
          duration: 7500,
        });
        quickCommandControl.reset();
      }
      if (match.groups!["notJustNow"]) {
        switch (match.groups!["baseDate"]) {
          case "!":
            baseDate = new Date();
            break;
          case "t":
            baseDate = this.swapForm.get("tradeDate")!.value;
            break;
          case "e":
            baseDate = this.swapForm.get("effectiveDate")!.value;
            break;
          case "m":
            baseDate = this.swapForm.get("terminationDate")!.value;
            break;
        }

        switch (match.groups!["addRemove"]) {
          case "-":
            addOrSubtract = (
              timeDiff: moment.Duration,
              amount: string,
              unit: any
            ) => timeDiff.subtract(amount, unit);
            break;
          case "+":
            addOrSubtract = (
              timeDiff: moment.Duration,
              amount: string,
              unit: any
            ) => timeDiff.add(amount, unit);
            break;
        }

        switch (match.groups!["dateMsd"].slice(-1)) {
          case "y":
            addOrSubtract(
              timeDiff,
              match.groups!["dateMsd"].slice(0, -1),
              "years"
            );
            break;
          case "m":
            addOrSubtract(
              timeDiff,
              match.groups!["dateMsd"].slice(0, -1),
              "months"
            );
            break;
          case "d":
            addOrSubtract(
              timeDiff,
              match.groups!["dateMsd"].slice(0, -1),
              "days"
            );
            break;
        }
        if (match.groups!["date2sd"]) {
          switch (match.groups!["date2sd"].slice(-1)) {
            case "m":
              addOrSubtract(
                timeDiff,
                match.groups!["dateMsd"].slice(0, -1),
                "months"
              );
              break;
            case "d":
              addOrSubtract(
                timeDiff,
                match.groups!["dateMsd"].slice(0, -1),
                "days"
              );
              break;
          }
        }
        if (match.groups!["dateLsd"]) {
          addOrSubtract(
            timeDiff,
            match.groups!["dateMsd"].slice(0, -1),
            "days"
          );
        }

        targetControl.setValue(moment(baseDate).add(timeDiff).toDate());
        targetControl.updateValueAndValidity();
        this._snackBar.open("Date set!", "OK", {
          horizontalPosition: "right",
          verticalPosition: "top",
          duration: 1500,
        });
        quickCommandControl.reset();
      }
    }
  }
}
