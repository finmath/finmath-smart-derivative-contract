import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router'
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';
import { MaterialImportsModule } from "./material-imports.module";
import { AppRoutingModule } from './app-routing.module';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from "@angular/common";
import { MAT_DATE_LOCALE } from "@angular/material/core";
import { ApiModule } from "./openapi/api.module"

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    LayoutModule,
    RouterModule,
    MaterialImportsModule,
    CommonModule,
    ApiModule,
  ],
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: 'it-IT'},
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
