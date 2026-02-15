import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolicitudSocio } from './solicitud-socio';

describe('SolicitudSocio', () => {
  let component: SolicitudSocio;
  let fixture: ComponentFixture<SolicitudSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SolicitudSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SolicitudSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
