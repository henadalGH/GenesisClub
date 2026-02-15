import { TestBed } from '@angular/core/testing';

import { PendientesServicios } from './pendientes-servicios';

describe('PendientesServicios', () => {
  let service: PendientesServicios;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PendientesServicios);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
