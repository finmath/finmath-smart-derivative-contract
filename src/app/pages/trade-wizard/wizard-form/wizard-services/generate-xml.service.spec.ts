import { TestBed } from '@angular/core/testing';

import { GenerateXmlService } from './generate-xml.service';

describe('GenerateXmlService', () => {
  let service: GenerateXmlService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GenerateXmlService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
