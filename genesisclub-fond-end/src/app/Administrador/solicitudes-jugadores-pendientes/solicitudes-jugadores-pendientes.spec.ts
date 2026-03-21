import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolicitudesJugadoresPendientes } from './solicitudes-jugadores-pendientes';

describe('SolicitudesJugadoresPendientes', () => {
  let component: SolicitudesJugadoresPendientes;
  let fixture: ComponentFixture<SolicitudesJugadoresPendientes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SolicitudesJugadoresPendientes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SolicitudesJugadoresPendientes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
