import { TestBed } from '@angular/core/testing';

import { InvitacionServicio } from './invitacion-servicio';

describe('InvitacionServicio', () => {
  let service: InvitacionServicio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InvitacionServicio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
