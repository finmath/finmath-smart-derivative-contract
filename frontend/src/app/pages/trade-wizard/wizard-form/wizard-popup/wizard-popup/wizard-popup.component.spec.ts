import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WizardPopupComponent } from './wizard-popup.component';

describe('WizardPopupComponent', () => {
  let component: WizardPopupComponent;
  let fixture: ComponentFixture<WizardPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WizardPopupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WizardPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
