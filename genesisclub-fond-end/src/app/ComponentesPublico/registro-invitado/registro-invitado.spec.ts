import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistroInvitado } from './registro-invitado';

describe('RegistroInvitado', () => {
  let component: RegistroInvitado;
  let fixture: ComponentFixture<RegistroInvitado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistroInvitado]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistroInvitado);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
