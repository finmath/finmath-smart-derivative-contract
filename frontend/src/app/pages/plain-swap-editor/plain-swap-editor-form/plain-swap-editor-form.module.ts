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

@NgModule({
  declarations: [
    PlainSwapEditorFormComponent,
    PlainSwapEditorShowXmlDialogComponent,
    ExcludeCounterpartyFilterPipe,
    PlainSwapEditorScheduleViewerComponent,
    PlainSwapEditorSymbolSelectorComponent,
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
