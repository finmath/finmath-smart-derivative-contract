import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { TextDialogData } from "../../wizard-form.component";

@Component({
  selector: "app-wizard-popup",
  templateUrl: "./wizard-popup.component.html",
  styleUrls: ["./wizard-popup.component.css"],
})
export class WizardPopupComponent {
  constructor(
    public dialogRef: MatDialogRef<WizardPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TextDialogData
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
