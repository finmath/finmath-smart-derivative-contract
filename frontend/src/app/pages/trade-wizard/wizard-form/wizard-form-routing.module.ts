import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WizardFormComponent } from './wizard-form.component';

const routes: Routes = [{ path: '', component: WizardFormComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WizardFormRoutingModule { }
