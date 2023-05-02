import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PlainSwapEditorFormComponent } from "./plain-swap-editor-form.component";

describe("PlainSwapEditorFormComponent", () => {
  let component: PlainSwapEditorFormComponent;
  let fixture: ComponentFixture<PlainSwapEditorFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlainSwapEditorFormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PlainSwapEditorFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
