import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IncioJugador } from './incio-jugador';

describe('IncioJugador', () => {
  let component: IncioJugador;
  let fixture: ComponentFixture<IncioJugador>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IncioJugador]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IncioJugador);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
