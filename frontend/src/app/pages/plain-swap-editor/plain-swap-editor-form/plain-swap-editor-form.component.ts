import { PlainSwapEditorSaveLoadDialogComponent, CombinedData } from "./plain-swap-editor-save-load-dialog/plain-swap-editor-save-load-dialog.component";
import { Component, OnInit } from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from "@angular/forms";
import { counterparties } from "../../../shared/form-data/counterparties";
import { Currency, currencies } from "../../../shared/form-data/currencies";
import {
  DayCountFraction,
  dayCountFractions,
} from "../../../shared/form-data/day-count-fractions";
import {
  FixingDayOffset,
  fixingDayOffsets,
} from "../../../shared/form-data/fixing-day-offsets";
import { paymentFrequencies } from "../../../shared/form-data/payment-frequencies";
import { PlainSwapEditorShowXmlDialogComponent } from "./plain-swap-editor-show-xml-dialog/plain-swap-editor-show-xml-dialog.component";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Counterparty } from "../../../openapi/model/counterparty";
import { HttpHeaders } from "@angular/common/http";
import { debounceTime } from "rxjs";
import * as moment from "moment";
import { PlainSwapEditorScheduleViewerComponent } from "./plain-swap-editor-schedule-viewer/plain-swap-editor-schedule-viewer.component";
import {
  CashflowPeriod,
  JsonMarketDataItem,
  PlainSwapEditorService,
  PlainSwapOperationRequest,
} from "src/app/openapi";
import { PlainSwapEditorSymbolSelectorComponent } from "./plain-swap-editor-symbol-selector/plain-swap-editor-symbol-selector.component";
import { PlainSwapEditorGeneratorSelectorComponent } from "./plain-swap-editor-generator-selector/plain-swap-editor-generator-selector.component";
import { PlainSwapEditorGenerator } from "src/app/shared/plain-swap-editor-generators/plain-swap-editor-generators";
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
  selector: "app-plain-swap-editor-form",
  templateUrl: "./plain-swap-editor-form.component.html",
  styleUrls: ["./plain-swap-editor-form.component.scss"],
})
export class PlainSwapEditorFormComponent implements OnInit {
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
  selectedSymbols: JsonMarketDataItem[] | undefined;
  swapMaturityString: string = "+?";
  startDelayString: string = "+?";
  generatorFileName: string | undefined;
  firstVaild: boolean = false;

  constructor(
    private _snackBar: MatSnackBar,
    private readonly plainSwapEditorService: PlainSwapEditorService,
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

    this.plainSwapEditorService.defaultHeaders = new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: "Basic " + window.btoa("user1:password1"),
    });

    this.swapForm = this._formBuilder.group({
      firstCounterparty: ["", Validators.required],
      secondCounterparty: ["", Validators.required],
      marginBufferAmount: [0.0, [Validators.required, Validators.min(0)]],
      terminationFeeAmount: [0.0, [Validators.required, Validators.min(0)]],
      notionalAmount: [0.0, Validators.min(0)],
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
        if (this.isAllControlsValid()) {
          (
            document
              .getElementsByClassName("currentNpvLabelArea")
              .item(0)!
              .childNodes.item(0) as HTMLAnchorElement
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
      valuationSymbols: this.selectedSymbols,
      currentGenerator: this.generatorFileName,
    } as PlainSwapOperationRequest;
  }

  pushXMLGenerationRequest() {
    this.plainSwapEditorService
      .generatePlainSwapSdcml(this.mapRequest(), "body", false, {
        httpHeaderAccept: "text/plain",
      })
      .subscribe({
        next: (sdcmlBody) => {
          this.dialogMessage = sdcmlBody;
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
    this.plainSwapEditorService.getFixedSchedule(this.mapRequest()).subscribe({
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

  pushParRateRequest() {
    (
      document
        .getElementsByClassName("currentNpvLabelArea")
        .item(0)!
        .childNodes.item(0) as HTMLAnchorElement
    ).innerHTML = "Current NPV: calculating par rate...";
    this.plainSwapEditorService.getParRate(this.mapRequest()).subscribe({
      next: (parRate) => {
        this.swapForm.get("fixedRate")!.setValue(parRate.toFixed(6));
        this.swapForm.get("fixedRate")!.updateValueAndValidity();
        this._snackBar.open("Par rate set!", "OK", {
          horizontalPosition: "right",
          verticalPosition: "top",
          duration: 3500,
        });
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
    this.plainSwapEditorService
      .getFloatingSchedule(this.mapRequest())
      .subscribe({
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

  pushPricingRequest() {
    this.plainSwapEditorService
      .evaluateFromPlainSwapEditor(this.mapRequest())
      .subscribe({
        next: (valueResponse) => {
          console.log(JSON.stringify(valueResponse));
          (
            document
              .getElementsByClassName("currentNpvLabelArea")
              .item(0)!
              .childNodes.item(0) as HTMLAnchorElement
          ).innerHTML =
            "Current NPV: " +
            valueResponse.value.toFixed(2) +
            this.currencyPrefix;
        },
        error: (error) => {
          (
            document
              .getElementsByClassName("currentNpvLabelArea")
              .item(0)!
              .childNodes.item(0) as HTMLAnchorElement
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
    return (
      this.swapForm.valid &&
      this.selectedSymbols &&
      this.selectedSymbols?.length > 0 &&
      !moment(this.swapForm.get("effectiveDate")!.value).isSameOrAfter(
        moment(this.swapForm.get("terminationDate")!.value)
      ) &&
      moment(this.swapForm.get("effectiveDate")!.value).isSameOrAfter(
        moment(this.swapForm.get("tradeDate")!.value)
      )
    );
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

      this.swapForm.get("fixedPayingParty")!.enable();
      this.swapForm.get("floatingPayingParty")!.enable();
    }
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
    const dialogRef = this.dialog.open(PlainSwapEditorShowXmlDialogComponent, {
      data: {
        dialogMessage: this.dialogMessage,
        dialogWindowTitle: this.dialogWindowTitle,
      },
      width: "80%",
      height: "80%",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }

  openTableDialog(tableData: CashflowPeriod[]): void {
    const dialogRef = this.dialog.open(PlainSwapEditorScheduleViewerComponent, {
      data: tableData,
      width: "80%",
      height: "80%",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }

  openSymbolSelection() {
    const dialogRef = this.dialog.open(PlainSwapEditorSymbolSelectorComponent, {
      data: this.selectedSymbols,
      width: "80%",
      height: "80%",
    });

    dialogRef
      .afterClosed()
      .subscribe((selectedSymbols: JsonMarketDataItem[]) => {
        this.selectedSymbols = selectedSymbols;
        if (this.isAllControlsValid()) {
          (
            document
              .getElementsByClassName("currentNpvLabelArea")
              .item(0)!
              .childNodes.item(0) as HTMLAnchorElement
          ).innerHTML = "Current NPV: loading...";
          this.pushPricingRequest();
        }
      });
  }

  openGeneratorSelection() {
    const dialogRef = this.dialog.open(
      PlainSwapEditorGeneratorSelectorComponent,
      {
        data: this.selectedSymbols,
        width: "80%",
        height: "80%",
      }
    );

    dialogRef
      .afterClosed()
      .subscribe((currentGenerator: PlainSwapEditorGenerator | undefined) => {
        if (currentGenerator) {
          this.selectedSymbols = currentGenerator.defaultSymbolsList;
          this.generatorFileName = currentGenerator.fileName;
          this.swapForm
            .get("floatingFixingDayOffset")!
            .setValue(currentGenerator.floatingFixingDayOffset.id);
          this.swapForm
            .get("floatingRateIndex")!
            .setValue(currentGenerator.indexName);
          this.swapForm
            .get("fixedPaymentFrequency")!
            .setValue(currentGenerator.fixedPaymentFrequency.fullName);
          this.swapForm
            .get("floatingPaymentFrequency")!
            .setValue(currentGenerator.floatingPaymentFrequency.fullName);
          this.swapForm
            .get("floatingDayCountFraction")!
            .setValue(currentGenerator.floatingDayCountFraction.id);
          this.swapForm
            .get("fixedDayCountFraction")!
            .setValue(currentGenerator.fixedDayCountFraction.id);
          this.swapForm.updateValueAndValidity();
        }
      });
  }

  openSaveLoadDialog() {
    this.plainSwapEditorService.getSavedContracts().subscribe((response) => {
      console.log(response);
      const dialogRef = this.dialog.open(
        PlainSwapEditorSaveLoadDialogComponent,
        {
          data: {data: response, contract: this.isAllControlsValid() ? this.mapRequest(): undefined} as CombinedData,
          width: "80%",
          height: "80%",
        }
      );
      dialogRef
        .afterClosed()
        .subscribe((currentSettings: PlainSwapOperationRequest | undefined) => {
          if (currentSettings) {
            this.selectedSymbols = currentSettings.valuationSymbols;
            this.generatorFileName = currentSettings.currentGenerator;
            this.swapForm
              .get("floatingFixingDayOffset")!
              .setValue(currentSettings.floatingFixingDayOffset.toString());
            this.swapForm
              .get("floatingRateIndex")!
              .setValue(currentSettings.floatingRateIndex);
            this.swapForm
              .get("fixedPaymentFrequency")!
              .setValue(currentSettings.fixedPaymentFrequency.fullName);
            this.swapForm
              .get("floatingPaymentFrequency")!
              .setValue(currentSettings.floatingPaymentFrequency.fullName);
            this.swapForm
              .get("floatingDayCountFraction")!
              .setValue(currentSettings.floatingDayCountFraction);
            this.swapForm
              .get("fixedDayCountFraction")!
              .setValue(currentSettings.fixedDayCountFraction);
            this.swapForm.get("fixedRate")!.setValue(currentSettings.fixedRate);
            this.swapForm
              .get("firstCounterparty")!
              .setValue(currentSettings.firstCounterparty.bicCode);
            this.swapForm.updateValueAndValidity();
            this.onFirstPartySelection();
            this.swapForm.get("tradeDate")!.setValue(currentSettings.tradeDate);
            this.swapForm
              .get("terminationDate")!
              .setValue(currentSettings.terminationDate);
            this.swapForm
              .get("effectiveDate")!
              .setValue(currentSettings.effectiveDate);
            this.swapForm
              .get("secondCounterparty")!
              .setValue(currentSettings.secondCounterparty.bicCode);
            this.swapForm.updateValueAndValidity();
            this.onSecondPartySelection();
            this.swapForm
              .get("marginBufferAmount")!
              .setValue(currentSettings.marginBufferAmount);
            this.swapForm
              .get("terminationFeeAmount")!
              .setValue(currentSettings.terminationFeeAmount);
            this.swapForm
              .get("floatingPayingParty")!
              .setValue(currentSettings.floatingPayingParty.bicCode);
            this.swapForm
              .get("fixedPayingParty")!
              .setValue(currentSettings.fixedPayingParty.bicCode);
            this.swapForm
              .get("notionalAmount")!
              .setValue(currentSettings.notionalAmount);
            this.onStartDelayChange();
            this.onMaturityChange();
            this.swapForm.updateValueAndValidity();
          }
        });
    });
  }
  onQuickCommandChange(
    _targetControl: AbstractControl | null,
    quickCommandControl: FormControl
  ): void {
    let quickCommand = quickCommandControl.value as string;
    let targetControl = _targetControl as FormControl;
    let baseDate: Date = moment()
      .utc(true)
      .hours(0)
      .minutes(0)
      .seconds(0)
      .milliseconds(0)
      .toDate();
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
                match.groups!["date2sd"].slice(0, -1),
                "months"
              );
              break;
            case "d":
              addOrSubtract(
                timeDiff,
                match.groups!["date2sd"].slice(0, -1),
                "days"
              );
              break;
          }
        }
        if (match.groups!["dateLsd"]) {
          addOrSubtract(
            timeDiff,
            match.groups!["dateLsd"].slice(0, -1),
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

  onMaturityChange() {
    if (
      this.swapForm.get("effectiveDate")!.value != "" &&
      this.swapForm.get("terminationDate")!.value != ""
    ) {
      let effectiveDateAsMoment = moment(
        this.swapForm.get("effectiveDate")!.value
      );
      let terminationDateAsMoment = moment(
        this.swapForm.get("terminationDate")!.value
      );
      let diff = moment.duration(
        terminationDateAsMoment.diff(effectiveDateAsMoment)
      );
      this.swapMaturityString =
        diff.years() + "y" + diff.months() + "m" + diff.days() + "d";
    }
  }

  onStartDelayChange() {
    if (
      this.swapForm.get("effectiveDate")!.value != "" &&
      this.swapForm.get("tradeDate")!.value != ""
    ) {
      let effectiveDateAsMoment = moment(
        this.swapForm.get("effectiveDate")!.value
      );
      let tradeDateAsMoment = moment(this.swapForm.get("tradeDate")!.value);
      let diff = moment.duration(effectiveDateAsMoment.diff(tradeDateAsMoment));

      if (diff.asDays() < 1 && diff.asMilliseconds() > 0) {
        this.startDelayString = "Spot";
      } else {
        this.startDelayString =
          "in " + diff.years() + "y" + diff.months() + "m" + diff.days() + "d";
      }
    }
  }

  useSuffixes(_targetControl: AbstractControl | null): void {
    let targetControl = _targetControl as FormControl;
    let quickCommand = targetControl.value as string;
    let multiplier = 1;
    if (!quickCommand.match("^\\d+[.]?\\d*[kmG]?$")) {
      this._snackBar.open("That's not how you write a number!", "OK", {
        horizontalPosition: "right",
        verticalPosition: "top",
        duration: 7500,
      });
      targetControl.reset();
      targetControl.setErrors({ incorrect: true });
    } else {
      switch (quickCommand.slice(-1)) {
        case "G":
          multiplier = 1_000_000_000;
          break;
        case "m":
          multiplier = 1_000_000;
          break;
        case "k":
          multiplier = 1_000;
          break;
      }

      targetControl.setValue(
        Number.parseFloat(
          multiplier == 1 ? quickCommand : quickCommand.slice(0, -1)
        ) * multiplier
      );
      targetControl.updateValueAndValidity();
    }
  }
}
