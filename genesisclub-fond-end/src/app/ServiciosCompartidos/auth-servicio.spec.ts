import { TestBed } from '@angular/core/testing';

import { AuthServicio } from './auth-servicio';

describe('AuthServicio', () => {
  let service: AuthServicio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthServicio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
