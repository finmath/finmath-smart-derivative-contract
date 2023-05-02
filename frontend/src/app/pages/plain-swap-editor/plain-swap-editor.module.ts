import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatStepperModule } from "@angular/material/stepper";
import { PlainSwapEditorRoutingModule } from "./plain-swap-editor-routing.module";
import { PlainSwapEditorComponent } from "./plain-swap-editor.component";
import { MatGridListModule } from "@angular/material/grid-list";
import { MatCardModule } from "@angular/material/card";
import { MatMenuModule } from "@angular/material/menu";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { LayoutModule } from "@angular/cdk/layout";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatToolbarModule } from "@angular/material/toolbar";

@NgModule({
  declarations: [PlainSwapEditorComponent],
  imports: [
    CommonModule,
    PlainSwapEditorRoutingModule,
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    LayoutModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatToolbarModule,
  ],
})
export class PlainSwapEditorModule {}
