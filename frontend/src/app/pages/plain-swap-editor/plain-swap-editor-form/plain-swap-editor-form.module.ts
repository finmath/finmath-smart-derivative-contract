import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatNativeDateModule } from "@angular/material/core";
import { PlainSwapEditorFormRoutingModule } from "./plain-swap-editor-form-routing.module";
import { PlainSwapEditorFormComponent } from "./plain-swap-editor-form.component";
import { MatStepperModule } from "@angular/material/stepper";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MaterialImportsModule } from "../../../material-imports.module";
import { MatTableModule } from "@angular/material/table";
import { STEPPER_GLOBAL_OPTIONS } from "@angular/cdk/stepper";
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from "@angular/material/form-field";
import { PlainSwapEditorShowXmlDialogComponent } from "./plain-swap-editor-show-xml-dialog/plain-swap-editor-show-xml-dialog.component";
import { ExcludeCounterpartyFilterPipe } from "../../../shared/exclude-counterparty-filter.pipe";
import { PlainSwapEditorScheduleViewerComponent } from "./plain-swap-editor-schedule-viewer/plain-swap-editor-schedule-viewer.component";
import { PlainSwapEditorSymbolSelectorComponent } from "./plain-swap-editor-symbol-selector/plain-swap-editor-symbol-selector.component";
import { PlainSwapEditorGeneratorSelectorComponent } from "./plain-swap-editor-generator-selector/plain-swap-editor-generator-selector.component";
import { PlainSwapEditorSaveLoadDialogComponent } from "./plain-swap-editor-save-load-dialog/plain-swap-editor-save-load-dialog.component";
import { PlainSwapEditorHistoryViewerComponent } from "./plain-swap-editor-symbol-selector/plain-swap-editor-history-viewer/plain-swap-editor-history-viewer.component";
import { PlainSwapEditorMarketDataManager } from "./plain-swap-editor-market-data-manager/plain-swap-editor-market-data-manager.component";

/**
 * Plain swap editor form module.
 * 
 * @TODO check if this arrangement is still optimal, may need to remove routing from the parent card.
 */
@NgModule({
  declarations: [
    PlainSwapEditorFormComponent,
    PlainSwapEditorShowXmlDialogComponent,
    ExcludeCounterpartyFilterPipe,
    PlainSwapEditorScheduleViewerComponent,
    PlainSwapEditorSymbolSelectorComponent,
    PlainSwapEditorGeneratorSelectorComponent,
    PlainSwapEditorSaveLoadDialogComponent,
    PlainSwapEditorHistoryViewerComponent,
    PlainSwapEditorMarketDataManager
  ],
  imports: [
    CommonModule,
    PlainSwapEditorFormRoutingModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    MatStepperModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MaterialImportsModule,
    MatTableModule,
  ],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { showError: true },
    },
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: { floatLabel: "always", appearance: "outline" },
    },
  ],
})
export class PlainSwapEditorFormModule {}
