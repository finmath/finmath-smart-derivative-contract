import { ExtraOptions, RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { EmptyRouteComponent } from "./empty-route/empty-route.component";
import { APP_BASE_HREF } from "@angular/common";
import { singleSpaPropsSubject } from "src/single-spa/single-spa-props";

let isStandalone = true;
singleSpaPropsSubject.subscribe((props: any) => {
  isStandalone = props.standalone;
});
let rts: Routes = [];
rts = [
  { path: "", redirectTo: "pages", pathMatch: "full" },
  {
    path: "pages",
    loadChildren: () =>
      import("./pages/pages.module").then((m) => m.PagesModule),
  },
  //{ path: "**", redirectTo: "pages" },
  { path: '**', component: EmptyRouteComponent }
];
export const routes = rts;

const config: ExtraOptions = {
  useHash: false,
};

@NgModule({
  imports: [RouterModule.forRoot(routes, config)],
  exports: [RouterModule],
  providers: [
    { provide: APP_BASE_HREF, useValue: '/' }
  ],
})
export class AppRoutingModule {}
