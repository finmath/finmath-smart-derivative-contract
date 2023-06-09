import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { PlainSwapEditorComponent } from "./plain-swap-editor.component";

const routes: Routes = [
  {
    path: "",
    component: PlainSwapEditorComponent,
    children: [
      {
        path: "wizard-form",
        loadChildren: () =>
          import("./plain-swap-editor-form/plain-swap-editor-form.module").then(
            (m) => m.PlainSwapEditorFormModule
          ),
        outlet: "wizardFormOutlet",
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PlainSwapEditorRoutingModule {}
