import { Component, OnInit } from '@angular/core';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-trade-wizard',
  templateUrl: './trade-wizard.component.html',
  styleUrls: ['./trade-wizard.component.css']
})
export class TradeWizardComponent implements OnInit {
  /** Based on the screen size, switch from standard to one column per row */
  cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return [
          { title: 'Trade data', cols: 1, rows: 1, outletName: 'wizardFormOutlet' },
        ];
      }

      return [
        { title: 'Trade data', cols: 2, rows: 2, outletName: 'wizardFormOutlet' },
      ];
    })
  );


  constructor(private _router: Router,
              private breakpointObserver: BreakpointObserver,
              private _activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    console.log("Routing to trade wizard!");
    this._router.navigate([{ outlets: { wizardFormOutlet: [ 'wizard-form' ] }}], {relativeTo:this._activatedRoute});
  }
}
