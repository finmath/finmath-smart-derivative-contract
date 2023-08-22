import { Component, Inject, ViewChild } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatSort } from "@angular/material/sort";
import { MarketDataSetValuesInner } from "src/app/openapi";


@Component({
  selector: "app-plain-swap-editor-history-viewer",
  templateUrl: "./plain-swap-editor-history-viewer.component.html",
  styleUrls: ["./plain-swap-editor-history-viewer.component.scss"],
})
export class PlainSwapEditorHistoryViewerComponent {
  displayedColumns: string[] = [
    "dataTimestamp",
    "value",
  ];

  constructor(
    public dialogRef: MatDialogRef<PlainSwapEditorHistoryViewerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MarketDataSetValuesInner[]
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  reformatRate(rate: number): string{
    return (rate*100).toFixed(3);
  }
}
