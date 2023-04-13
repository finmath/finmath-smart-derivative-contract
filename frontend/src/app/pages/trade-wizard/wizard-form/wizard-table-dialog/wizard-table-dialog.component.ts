import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { CashflowPeriod } from "src/app/openapi/model/cashflowPeriod";

@Component({
  selector: "app-wizard-table-dialog",
  templateUrl: "./wizard-table-dialog.component.html",
  styleUrls: ["./wizard-table-dialog.component.scss"],
})
export class WizardTableDialogComponent {
  displayedColumns: string[] = [
    "fixingDate",
    "paymentDate",
    "periodStart",
    "periodEnd",
    "cashflow",
    "rate"
  ];

  constructor(
    public dialogRef: MatDialogRef<WizardTableDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CashflowPeriod[]
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
