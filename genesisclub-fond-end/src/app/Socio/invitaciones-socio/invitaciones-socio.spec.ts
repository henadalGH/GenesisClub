import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvitacionesSocio } from './invitaciones-socio';

describe('InvitacionesSocio', () => {
  let component: InvitacionesSocio;
  let fixture: ComponentFixture<InvitacionesSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvitacionesSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvitacionesSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
