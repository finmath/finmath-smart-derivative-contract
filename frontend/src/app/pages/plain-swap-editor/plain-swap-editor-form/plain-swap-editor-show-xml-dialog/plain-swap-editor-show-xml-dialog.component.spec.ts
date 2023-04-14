import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PlainSwapEditorShowXmlDialogComponent } from "./plain-swap-editor-show-xml-dialog.component";

describe("PlainSwapEditorShowXmlDialogComponent", () => {
  let component: PlainSwapEditorShowXmlDialogComponent;
  let fixture: ComponentFixture<PlainSwapEditorShowXmlDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlainSwapEditorShowXmlDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PlainSwapEditorShowXmlDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
