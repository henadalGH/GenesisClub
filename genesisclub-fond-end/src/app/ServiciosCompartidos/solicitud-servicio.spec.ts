import { TestBed } from '@angular/core/testing';

import { SolicitudServicio } from './solicitud-servicio';

describe('SolicitudServicio', () => {
  let service: SolicitudServicio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SolicitudServicio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
