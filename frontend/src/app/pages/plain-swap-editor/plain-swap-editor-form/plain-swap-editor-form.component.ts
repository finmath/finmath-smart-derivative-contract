import { DefaultService } from "./../../../openapi/api/default.service";
import {
  PlainSwapEditorSaveLoadDialogComponent,
  SaveLoadInteractionData,
} from "./plain-swap-editor-save-load-dialog/plain-swap-editor-save-load-dialog.component";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
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
import { HttpHeaders, HttpClient } from "@angular/common/http";
import { debounceTime, first, firstValueFrom, interval, map } from "rxjs";
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

/**
 * Interface that represents the data that is passed to a pure text dialog box
 */
export interface TextDialogData {
  dialogMessage: string;
  dialogWindowTitle: string;
}

/**
 * Header options to be used when calling the valuation service API.#
 *
 * @TODO once the authentication system is in place, remove this plain text storage of username and password.
 */
const httpOptions = {
  headers: new HttpHeaders({
    "Content-Type": "application/json",
    Authorization: "Basic " + window.btoa("user1:password1"),
  }),
};

/**
 * Plain swap editor form component.
 *
 * @TODO check if this arrangement is still optimal, may need to remove routing from the parent card.
 */
@Component({
  selector: "app-plain-swap-editor-form",
  templateUrl: "./plain-swap-editor-form.component.html",
  styleUrls: ["./plain-swap-editor-form.component.scss"],
})
export class PlainSwapEditorFormComponent implements OnInit, AfterViewInit {
  protected serverStatusMsg: string = "Server status is UNKNOWN.";
  protected npvlabel: string = "Current NPV: waiting for data...";

  /**
   * Main form control group, excludes the quick command bars.
   */
  protected swapForm: FormGroup;

  /**
   * Currency prefix to be used throughout the UI.
   */
  protected currencyPrefix: string;

  /**
   * List of counterparties available for interaction.
   */
  protected counterparties = counterparties;
  /**
   * List of currencies available for interaction.
   */
  protected currencies = currencies;
  /**
   * List of day count fractions available for interaction.
   */
  protected dayCountFractions = dayCountFractions;
  /**
   * List of payment frequency available for interaction.
   */
  protected paymentFrequencies = paymentFrequencies;
  /**
   * List of fixing offsets available for interaction.
   */
  protected fixingDayOffsets = fixingDayOffsets;

  /**
   * Dialog pop up message.
   */
  private dialogMessage: string | undefined;

  /**
   * Dialog pop up title.
   */
  private dialogWindowTitle: string | undefined;
  /**
   * Currently selected parties
   */
  protected selectedParties = [] as Counterparty[];

  /**
   * Indicates where the first (home) party has been selected.
   */
  private firstPartySelected = false;
  /**
   * Indicates where the second party has been selected.
   */
  private secondPartySelected = false;
  protected terminationDateQuickCommand: FormControl;
  protected tradeDateQuickCommand: FormControl;
  protected effectiveDateQuickCommand: FormControl;
  /**
   * Time arithmetic quick commands parser regex.
   */
  private quickCommandRegExp: RegExp =
    /(?<now>^\!$)|(?<notJustNow>^(?<baseDate>[\!tem])?(?<addRemove>[\+\-])(?<dateMsd>\d+[ymd])(?<date2sd>\d+[md])?(?<dateLsd>\d+d)?$)/gm;

  /**
   * List of currently selected valuation symbols.
   */
  protected selectedSymbols: JsonMarketDataItem[] | undefined;

  /**
   * String that contains the effective-maturity timespan length.
   */
  protected swapMaturityString: string = "+?";

  /**
   * String that contains the trade-effective timespan length.
   */
  protected startDelayString: string = "+?";

  /**
   * Currently selected generator filename.
   */
  protected generatorFileName: string | undefined;

  constructor(
    private _snackBar: MatSnackBar,
    private readonly plainSwapEditorService: PlainSwapEditorService,
    private readonly defaultService: DefaultService,
    private httpClient: HttpClient,
    public dialog: MatDialog,
    private _formBuilder: FormBuilder
  ) {
    this.swapForm = this._formBuilder.group({});
    this.currencyPrefix = "€";
    this.terminationDateQuickCommand = this._formBuilder.control("");
    this.tradeDateQuickCommand = this._formBuilder.control("");
    this.effectiveDateQuickCommand = this._formBuilder.control("");
  }

  ngAfterViewInit() {
    let serverstatus = document.getElementById(
      "serverstatus"
    ) as HTMLAnchorElement;
    this.source.subscribe(() => {
      this.defaultService
        .infoFinmath("response")
        .pipe(first())
        .subscribe({
          next: (r) => {
            if (r.ok) {
              serverstatus.classList.remove("server-unavail");
              serverstatus.classList.add("server-active");
              this.serverStatusMsg =
                "Server status is AVAILABLE. Double click to upload market data.";
            } else {
              serverstatus.classList.add("server-unavail");
              serverstatus.classList.remove("server-active");
              this.serverStatusMsg = "Server status is UNAVAILABLE.";
            }
          },
          error: (e) => {
            serverstatus.classList.add("server-unavail");
            serverstatus.classList.remove("server-active");
            this.serverStatusMsg = "Server status is UNAVAILABLE.";
          },
        });
    });
  }

  /**
   * Strips the time zone information from a date while preserving the local time.
   * @param {Date} date Date for which the timezone info must be removed.
   * @returns {Date} the input date interpreted as UTC.
   *
   * @TODO explore whether there is a better way to do this now that moment.js is imported (related to the larger issue of dates handling).
   */
  interpretAsUTC(date: Date): Date {
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

    this.defaultService.defaultHeaders = new HttpHeaders({
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
          this.npvlabel = "Current NPV: loading...";
          this.pushPricingRequest();
        }
      });
  }

  /**
   * Scans the main form group and the component object to create a PlainSwapOperationRequest.
   * @returns {PlainSwapOperationRequest} the resulting request.
   */
  mapRequest(): PlainSwapOperationRequest {
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

  /**
   * Scans the main form to ask the backend service for the SDCmL file relative to the contract.
   */
  pushXMLGenerationRequest(): void {
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
            "Something went wrong. A developer might want to know about the stuff in the console log.",
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

  /**
   * Scans the main form to ask the backend service for the payment schedule relative to the fixed leg.
   */
  pushFixedScheduleGenerationRequest(): void {
    this.plainSwapEditorService.getFixedSchedule(this.mapRequest()).subscribe({
      next: (cashflowPeriods) => {
        this.dialogMessage = JSON.stringify(cashflowPeriods);
        this.dialogWindowTitle = "Your SDCmL document:";
        this.openPaymentScheduleDialog(cashflowPeriods);
      },
      error: (error) => {
        this._snackBar.open(
          "Something went wrong. A developer might want to know about the stuff in the console log.",
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

  /**
   * Scans the main form to ask the backend service for the par rate.
   */
  pushParRateRequest(): void {
    this.npvlabel = "Current NPV: calculating par rate..."; // @TODO this is not a reliable way to interact with the NPV element. Maybe use references?
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
          "Something went wrong. A developer might want to know about the stuff in the console log.",
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

  /**
   * Scans the main form to ask the backend service for the payment schedule relative to the floating leg.
   */
  pushFloatingScheduleGenerationRequest(): void {
    this.plainSwapEditorService
      .getFloatingSchedule(this.mapRequest())
      .subscribe({
        next: (cashflowPeriods) => {
          this.dialogMessage = JSON.stringify(cashflowPeriods);
          this.dialogWindowTitle = "Your SDCmL document:";
          this.openPaymentScheduleDialog(cashflowPeriods);
        },
        error: (error) => {
          this._snackBar.open(
            "Something went wrong. A developer might want to know about the stuff in the console log.",
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

  /**
   * Scans the main form to ask the backend service for the current NPV of the contract.
   */
  pushPricingRequest() {
    this.plainSwapEditorService
      .evaluateFromPlainSwapEditor(this.mapRequest())
      .subscribe({
        next: (valueResponse) => {
          console.log(JSON.stringify(valueResponse));
          this.npvlabel =
            "Current NPV: " +
            valueResponse.value.toFixed(2) + //@TODO use the number of decimals specified in the currency object
            this.currencyPrefix;
        },
        error: (error) => {
          this.npvlabel = "Current NPV: last valuation failed!";
          this._snackBar.open(
            "Something went wrong. A developer might want to know about the stuff in the console log.",
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

  /**
   * Scans the main form to check whether there is enough information to interact with the valuation server.
   *
   * @TODO this is clunky, maybe writing some custom validators is better?
   */
  isAllControlsValid(): boolean {
    let selectionValid =
      !!this.selectedSymbols && this.selectedSymbols?.length > 0;
    let formValid: boolean =
      selectionValid &&
      this.swapForm.valid &&
      !moment(this.swapForm.get("effectiveDate")!.value).isSameOrAfter(
        moment(this.swapForm.get("terminationDate")!.value)
      ) &&
      moment(this.swapForm.get("effectiveDate")!.value).isSameOrAfter(
        moment(this.swapForm.get("tradeDate")!.value)
      );
    return formValid;
  }

  /**
   * Changes the currency prefix in the UI on currency selection change.
   */
  onCurrencyChange(): void {
    let currencyCode = this.swapForm.get("currency")!.value;
    this.currencyPrefix = this.currencies.find(
      (c: Currency) => c.code === currencyCode
    )!.symbol;
  }

  /**
   * Checks whether the home party has been selected and updates internal state accordingly.
   *
   * @TODO seems kind of clunky, but I have no idea on how to make it more streamlined. I miss Java:(
   */
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

  /**
   * Checks whether the second party has been selected and updates internal state accordingly.
   *
   * @TODO seems kind of clunky, but I have no idea on how to make it more streamlined. I miss Java:(
   */
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

  /**
   * Checks whether the main form is trying to define a contract with the same parties both as originating and receiving.
   *
   * @TODO clunky, could definitely be a custom validator.
   */
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

  /**
   * Checks internal state and opens a plain text dialog.
   */
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

  /**
   * Opens a payment schedule dialog.
   *
   * @param paymentScheduleData the payment schedule to be shown within the dialog.
   */
  openPaymentScheduleDialog(paymentScheduleData: CashflowPeriod[]): void {
    const dialogRef = this.dialog.open(PlainSwapEditorScheduleViewerComponent, {
      data: paymentScheduleData,
      width: "80%",
      height: "80%",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }

  /**
   * Opens the symbol selection dialog.
   */
  openSymbolSelection(): void {
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
          this.npvlabel = "Current NPV: loading...";
          this.pushPricingRequest();
        }
      });
  }

  /**
   * Opens the generator selection dialog.
   */
  openGeneratorSelection(): void {
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

  /**
   * Opens the save/load selection dialog.
   */
  openSaveLoadDialog(): void {
    this.plainSwapEditorService.getSavedContracts().subscribe((response) => {
      console.log(response);
      const dialogRef = this.dialog.open(
        PlainSwapEditorSaveLoadDialogComponent,
        {
          data: {
            serverStoredSavedContracts: response,
            currentContractFromEditor: this.isAllControlsValid()
              ? this.mapRequest()
              : undefined,
          } as SaveLoadInteractionData,
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

  /**
   * Parses time arithmetic quick commands
   * @param _targetControl the datepicker that acts as the operand.
   * @param quickCommandControl the quick command input that acts as the operator.
   */
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

  /**
   * Recalculates the effective-maturity time difference and updates view accodingly.
   */
  onMaturityChange(): void {
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

  /**
   * Recalculates the effective-trade time difference and updates view accodingly.
   */
  onStartDelayChange(): void {
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

  /**
   * Converts a text input in the format number or number-suffix and assigns it as number to the input itself.
   * @param _targetControl the control containing the number or number-suffix info.
   */
  useSuffixes(_targetControl: AbstractControl | null): void {
    let targetControl = _targetControl as FormControl;
    let quickCommand = targetControl.value as string;
    let multiplier = 1;
    if (!quickCommand.match("^\\d+[.]?\\d*[kmG]?$")) {
      this._snackBar.open("Bad syntax.", "OK", {
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

  private source = interval(1000);

  uploadMarketData(_event: Event): void {
    const event = _event.target as HTMLInputElement;
    const fileToUpload = event.files!.item(0);
    var fd = new FormData();
    fd.append("tradeData", fileToUpload as Blob);
    this.httpClient
      .post<string>(
        "http://34.159.3.234:8080/plain-swap-editor/upload-market-data",
        fd,
        {
          headers: new HttpHeaders({
            Authorization: "Basic " + window.btoa("user1:password1"), // @TODO: this is a clear-text password. It was necessary during testing, but obsviously this is not a good soultion!
            Accept: "text/plain",
          }),
          responseType: "text" as "json",
        }
      )
      .subscribe({
        next: (r) => this.isAllControlsValid() && this.pushPricingRequest(),
        error: (e) => {
          this._snackBar.open(
            "The server did not accept your market data. A developer might want to know about the stuff in the console log.",
            "OK",
            {
              horizontalPosition: "right",
              verticalPosition: "top",
              duration: 7500,
            }
          );
          console.log(JSON.stringify(e));
        },
      });
  }
}
