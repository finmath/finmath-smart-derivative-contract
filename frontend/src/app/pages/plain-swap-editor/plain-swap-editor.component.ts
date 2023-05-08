import { Component } from "@angular/core";
import { map } from "rxjs/operators";
import { Breakpoints, BreakpointObserver } from "@angular/cdk/layout";
import {
  ActivatedRoute,
  NavigationEnd,
  NavigationSkipped,
  Router,
} from "@angular/router";

/**
 * Plain swap editor component.
 */
@Component({
  selector: "app-plain-swap-editor",
  templateUrl: "./plain-swap-editor.component.html",
  styleUrls: ["./plain-swap-editor.component.scss"],
})
export class PlainSwapEditorComponent {
  /** Based on the screen size, switch from standard to one column per row */
  cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return [
          {
            title: "Trade data",
            cols: 1,
            rows: 1,
            outletName: "wizardFormOutlet",
          },
        ];
      }

      return [
        {
          title: "Trade data",
          cols: 2,
          rows: 2,
          outletName: "wizardFormOutlet",
        },
      ];
    })
  );

  constructor(
    private _router: Router,
    private breakpointObserver: BreakpointObserver,
    private _activatedRoute: ActivatedRoute
  ) {
    this._router.events.subscribe((event) => {
      //@TODO: this might be a suboptimal solution.
      if (
        event instanceof NavigationEnd ||
        event instanceof NavigationSkipped
      ) {
        if (event.url === "/pages/trade-wizard") {
          this._router.navigate(
            [{ outlets: { wizardFormOutlet: ["wizard-form"] } }],
            { relativeTo: this._activatedRoute }
          );
        }
      }
    });
  }
}
