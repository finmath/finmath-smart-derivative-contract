import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { PlainSwapEditorFormComponent } from "./plain-swap-editor-form.component";

const routes: Routes = [{ path: "", component: PlainSwapEditorFormComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PlainSwapEditorFormRoutingModule {}
