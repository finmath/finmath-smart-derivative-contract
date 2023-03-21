import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatNativeDateModule} from '@angular/material/core';
import { WizardFormRoutingModule } from './wizard-form-routing.module';
import { WizardFormComponent } from './wizard-form.component';
import { MatStepperModule } from '@angular/material/stepper';
import {MatInputModule} from '@angular/material/input'
import {MatButtonModule} from "@angular/material/button";
import { MaterialImportsModule } from "../../../material-imports.module";
import {STEPPER_GLOBAL_OPTIONS} from "@angular/cdk/stepper";
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from "@angular/material/form-field";
import { WizardPopupComponent } from './wizard-popup/wizard-popup/wizard-popup.component';
import {ExcludeFilterPipe} from "../../../shared/exclude-filter.pipe";


@NgModule({
  declarations: [
    WizardFormComponent,
    WizardPopupComponent,
    ExcludeFilterPipe,
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
    ],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { showError: true }
    },
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {floatLabel: 'always'}
    },
  ]
})
export class WizardFormModule { }
