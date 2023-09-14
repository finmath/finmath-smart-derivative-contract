import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material/table";
import { marketDataItems } from "../../../../shared/form-data/market-data-items";
import { JsonMarketDataItem, MarketDataTransferMessage } from "src/app/openapi";
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialog,
} from "@angular/material/dialog";
import { Inject, AfterViewInit, Component, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { PlainSwapEditorHistoryViewerComponent } from "./plain-swap-editor-history-viewer/plain-swap-editor-history-viewer.component";

@Component({
  selector: "app-plain-swap-editor-symbol-selector",
  styleUrls: ["./plain-swap-editor-symbol-selector.component.scss"],
  templateUrl: "./plain-swap-editor-symbol-selector.component.html",
})
export class PlainSwapEditorSymbolSelectorComponent implements AfterViewInit {
  displayedColumns: string[] = [
    "select",
    "symbol",
    "curve",
    "tenor",
    "itemType",
  ];
  marketDataItems: JsonMarketDataItem[] = marketDataItems;
  dataSource = new MatTableDataSource<JsonMarketDataItem>(this.marketDataItems);
  values = this.data[1] as MarketDataTransferMessage;
  selection: SelectionModel<JsonMarketDataItem>;
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
  checkboxLabel(row?: JsonMarketDataItem): string {
    if (!row) {
      return `${this.isAllSelected() ? "deselect" : "select"} all`;
    }
    return `${this.selection.isSelected(row) ? "deselect" : "select"} row ${
      row.symbol
    }`;
  }

  constructor(
    public dialogRef: MatDialogRef<PlainSwapEditorSymbolSelectorComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any[],
    public dialog: MatDialog
  ) {
    this.selection = new SelectionModel<JsonMarketDataItem>(
      true,
      this.data[0] as JsonMarketDataItem[]
    );
  }

  onClose(): void {
    this.dialogRef.close(this.selection.selected);
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

  getLastQuote(symbol: string) {
    let matches = this.values.values.filter((x) => x.symbol == symbol)
    .sort((a,b)=>{ return new Date(a.dataTimestamp!) > new Date(b.dataTimestamp!)? -1: 1});
    return (matches[0].value! * 100).toFixed(3);
  }

  getLastUpdate(symbol: string) {
    let matches = this.values.values.filter((x) => x.symbol == symbol)
    .sort((a,b)=>{ return new Date(a.dataTimestamp!) > new Date(b.dataTimestamp!)? -1: 1});
    return matches[0].dataTimestamp;
  }

  getHistory(symbol: string) {
    let matches = this.values.values.filter((x) => x.symbol == symbol)
    .sort((a,b)=>{ return new Date(a.dataTimestamp!) > new Date(b.dataTimestamp!)? -1: 1});
    this.openHistoryDialog(matches);
  }

  openHistoryDialog(data: any): void {
    const dialogRef = this.dialog.open(PlainSwapEditorHistoryViewerComponent, {
      data,
      width: "80%",
      height: "80%",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }
}
