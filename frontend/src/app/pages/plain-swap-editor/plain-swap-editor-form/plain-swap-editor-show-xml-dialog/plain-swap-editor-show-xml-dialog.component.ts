import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { TextDialogData } from "../plain-swap-editor-form.component";

@Component({
  selector: "app-plain-swap-editor-show-xml-dialog",
  templateUrl: "./plain-swap-editor-show-xml-dialog.component.html",
  styleUrls: ["./plain-swap-editor-show-xml-dialog.component.css"],
})
export class PlainSwapEditorShowXmlDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<PlainSwapEditorShowXmlDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TextDialogData
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
