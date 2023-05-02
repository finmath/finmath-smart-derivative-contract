import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { CashflowPeriod } from "src/app/openapi/model/cashflowPeriod";

@Component({
  selector: "app-plain-swap-editor-schedule-viewer",
  templateUrl: "./plain-swap-editor-schedule-viewer.component.html",
  styleUrls: ["./plain-swap-editor-schedule-viewer.component.scss"],
})
export class PlainSwapEditorScheduleViewerComponent {
  displayedColumns: string[] = [
    "fixingDate",
    "paymentDate",
    "periodStart",
    "periodEnd",
    "cashflow",
    "rate",
  ];

  constructor(
    public dialogRef: MatDialogRef<PlainSwapEditorScheduleViewerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CashflowPeriod[]
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
