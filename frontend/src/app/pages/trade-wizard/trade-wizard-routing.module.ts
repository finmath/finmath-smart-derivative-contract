import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TradeWizardComponent } from './trade-wizard.component';

const routes: Routes = [
  { path: '',
    component: TradeWizardComponent,
    children: [
      {
        path: 'wizard-form',
        loadChildren: () => import('./wizard-form/wizard-form.module').then(m => m.WizardFormModule),
        outlet: 'wizardFormOutlet',
      }
    ],
  }
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TradeWizardRoutingModule { }
