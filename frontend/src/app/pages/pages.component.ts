import { Component } from "@angular/core";
import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { Observable } from "rxjs";
import { map, shareReplay } from "rxjs/operators";

/**
 * Navigation bar and master viewport component.
 */
@Component({
  selector: "app-pages",
  templateUrl: "./pages.component.html",
  styleUrls: ["./pages.component.scss"],
})
export class PagesComponent {
  public isStandalone: boolean = true;
  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver) {
    /*singleSpaPropsSubject.subscribe((props: any) => {
      this.isStandalone = props.standalone;
      window.addEventListener('isSignedIn',(event: any) => {
        if(event.detail === false) {
            window.location.href = '/';
        }
      });
    });*/
  }
}
