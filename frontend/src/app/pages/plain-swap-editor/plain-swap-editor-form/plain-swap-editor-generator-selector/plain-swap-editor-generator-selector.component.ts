import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material/table";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { Inject, AfterViewInit, Component, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import {
  PlainSwapEditorGenerator,
  plainSwapEditorGenerators,
} from "src/app/shared/plain-swap-editor-generators/plain-swap-editor-generators";

@Component({
  selector: "app-plain-swap-editor-save-load-dialog",
  styleUrls: ["./plain-swap-editor-generator-selector.component.scss"],
  templateUrl: "./plain-swap-editor-generator-selector.component.html",
})
export class PlainSwapEditorGeneratorSelectorComponent
  implements AfterViewInit
{
  displayedColumns: string[] = ["select", "name", "fileName"];
  plainSwapEditorGenerators: PlainSwapEditorGenerator[] =
    plainSwapEditorGenerators;
  dataSource = new MatTableDataSource<PlainSwapEditorGenerator>(
    this.plainSwapEditorGenerators
  );
  selection: SelectionModel<PlainSwapEditorGenerator>;
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
  checkboxLabel(row?: PlainSwapEditorGenerator): string {
    if (!row) {
      return `${this.isAllSelected() ? "deselect" : "select"} all`;
    }
    return `${this.selection.isSelected(row) ? "deselect" : "select"} row ${
      row.name
    }`;
  }

  constructor(
    public dialogRef: MatDialogRef<PlainSwapEditorGeneratorSelectorComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PlainSwapEditorGenerator[]
  ) {
    this.selection = new SelectionModel<PlainSwapEditorGenerator>(
      false,
      this.data
    );
  }

  onClose(): void {
    if (this.selection.selected.length)
      this.dialogRef.close(this.selection.selected[0]);
    else this.dialogRef.close();
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
