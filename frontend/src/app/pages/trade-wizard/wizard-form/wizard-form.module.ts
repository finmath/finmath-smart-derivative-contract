import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatNativeDateModule } from "@angular/material/core";
import { WizardFormRoutingModule } from "./wizard-form-routing.module";
import { WizardFormComponent } from "./wizard-form.component";
import { MatStepperModule } from "@angular/material/stepper";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MaterialImportsModule } from "../../../material-imports.module";
import { MatTableModule } from "@angular/material/table";
import { STEPPER_GLOBAL_OPTIONS } from "@angular/cdk/stepper";
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from "@angular/material/form-field";
import { WizardPopupComponent } from "./wizard-popup/wizard-popup/wizard-popup.component";
import { ExcludeCounterpartyFilterPipe } from "../../../shared/exclude-filter.pipe";
import { WizardTableDialogComponent } from "./wizard-table-dialog/wizard-table-dialog.component";
import { WizardSymbolsDialogComponent } from "./wizard-symbols-dialog/wizard-symbols-dialog.component";

@NgModule({
  declarations: [
    WizardFormComponent,
    WizardPopupComponent,
    ExcludeCounterpartyFilterPipe,
    WizardTableDialogComponent,
    WizardSymbolsDialogComponent,
  ],
  imports: [
    CommonModule,
    WizardFormRoutingModule,
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
export class WizardFormModule {}
