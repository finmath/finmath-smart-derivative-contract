import { SaveContractRequest } from './../../../../openapi/model/saveContractRequest';
import { PlainSwapOperationRequest } from "./../../../../openapi/model/plainSwapOperationRequest";
import { MatSnackBar } from "@angular/material/snack-bar";
import { PlainSwapEditorService } from "./../../../../openapi/api/plainSwapEditor.service";
import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material/table";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { Inject, AfterViewInit, Component, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { FormControl } from "@angular/forms";

export interface CombinedData {
  data: string[];
  contract: PlainSwapOperationRequest | undefined;
}

@Component({
  selector: "app-plain-swap-editor-save-load-dialog",
  styleUrls: ["./plain-swap-editor-save-load-dialog.component.scss"],
  templateUrl: "./plain-swap-editor-save-load-dialog.component.html",
})
export class PlainSwapEditorSaveLoadDialogComponent implements AfterViewInit {
  displayedColumns: string[] = ["select", "name"];
  dataSource: MatTableDataSource<string>;
  selection: SelectionModel<string>;
  saveAsInput = new FormControl();
  data: string[];
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: string): string {
    if (!row) {
      return `${this.isAllSelected() ? "deselect" : "select"} all`;
    }
    return `${
      this.selection.isSelected(row) ? "deselect" : "select"
    } row ${row}`;
  }

  constructor(
    private _snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<PlainSwapEditorSaveLoadDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public combinedData: CombinedData,
    private plainSwapEditorService: PlainSwapEditorService
  ) {
    this.data = combinedData.data;
    this.selection = new SelectionModel<string>(false);
    this.dataSource = new MatTableDataSource<string>(this.data);
    this.dataSource.filterPredicate = (data: string, filter: string) => {
      return data.includes(filter);
    };
  }

  onClose(): void {
    if (this.selection.selected.length)
      this.plainSwapEditorService
        .loadContract(this.selection.selected[0])
        .subscribe((response) => {
          this.dialogRef.close(response);
        });
    else this.dialogRef.close();
  }

  onSaveAndClose(): void {
    if (this.combinedData.contract)
      this.plainSwapEditorService
        .saveContract({
          name: this.saveAsInput.value,
          plainSwapOperationRequest: this.combinedData.contract,
        } as SaveContractRequest, "body", false, {
          httpHeaderAccept: "text/plain",
        })
        .subscribe((response) => {
          this._snackBar.open(response, "OK", {
            horizontalPosition: "right",
            verticalPosition: "top",
            duration: 7500,
          });
          this.dialogRef.close();
        });
    else
      this._snackBar.open("Not allowed.", "OK", {
        horizontalPosition: "right",
        verticalPosition: "top",
        duration: 7500,
      });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
