import { TestBed } from '@angular/core/testing';

import { LoginServicio } from './login-servicio';

describe('LoginServicio', () => {
  let service: LoginServicio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoginServicio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
