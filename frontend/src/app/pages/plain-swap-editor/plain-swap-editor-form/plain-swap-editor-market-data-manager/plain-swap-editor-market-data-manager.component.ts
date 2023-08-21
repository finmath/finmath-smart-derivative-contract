import { SaveContractRequest } from "../../../../openapi/model/saveContractRequest";
import { PlainSwapOperationRequest } from "../../../../openapi/model/plainSwapOperationRequest";
import { MatSnackBar } from "@angular/material/snack-bar";
import { PlainSwapEditorService } from "../../../../openapi/api/plainSwapEditor.service";
import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material/table";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { Inject, AfterViewInit, Component, ViewChild, EventEmitter } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from "@angular/forms";

export interface MarketDataInteractionData {
  serverStoredUserMarketData: string[];
  latestDataset: string;
  isUsingLiveFeed: boolean;
}

@Component({
  selector: "app-plain-swap-editor-market-data-manager",
  styleUrls: ["./plain-swap-editor-market-data-manager.scss"],
  templateUrl: "./plain-swap-editor-market-data-manager.html",
})
export class PlainSwapEditorMarketDataManager implements AfterViewInit {
  displayedColumns: string[] = ["select", "name"];
  dataSource: MatTableDataSource<string>;
  selection: SelectionModel<string>;
  radioGroup: FormGroup;
  data: string[];
  onUpload = new EventEmitter(true);
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
    public dialogRef: MatDialogRef<PlainSwapEditorMarketDataManager>,
    @Inject(MAT_DIALOG_DATA) public combinedData: MarketDataInteractionData,
    private plainSwapEditorService: PlainSwapEditorService,
    private _formBuilder: FormBuilder
  ) {
    this.radioGroup = this._formBuilder.group({
      useLiveFeed: [
        combinedData.isUsingLiveFeed.toString(),
        Validators.required,
      ],
    });
    this.data = combinedData.serverStoredUserMarketData;
    this.selection = new SelectionModel<string>(false);
    this.dataSource = new MatTableDataSource<string>(this.data);
    this.selection.select("active_dataset.json");
    this.dataSource.filterPredicate = (data: string, filter: string) => {
      return data.includes(filter);
    };
  }

  onClose(): void {
    if (this.radioGroup.get("useLiveFeed")!.value === "false") {
      if (this.selection.selected.length)
        this.plainSwapEditorService
          .changeDataset(
            this.selection.selected[0] == "active_dataset.json"
              ? "USELIVE"
              : this.selection.selected[0],
            "body",
            false,
            {
              httpHeaderAccept: "text/plain",
            }
          )
          .subscribe((response) => {
            this.dialogRef.close(
              this.radioGroup.get("useLiveFeed")!.value === "false"
                ? false
                : true
            );
          });
    } else
      this.plainSwapEditorService
        .changeDataset("USELIVE", "body", false, {
          httpHeaderAccept: "text/plain",
        })
        .subscribe((response) => {
          this.dialogRef.close(
            this.radioGroup.get("useLiveFeed")!.value === "false" ? false : true
          );
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

  isForbiddenString(name: any) {
    return (name as string) === "active_dataset.json";
  }

  relayUpload(event: Event){
    this.onUpload.emit(event);
  }
}
