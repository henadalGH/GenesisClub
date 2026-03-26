import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntrarRubroJugador } from './entrar-rubro-jugador';

describe('EntrarRubroJugador', () => {
  let component: EntrarRubroJugador;
  let fixture: ComponentFixture<EntrarRubroJugador>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntrarRubroJugador]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntrarRubroJugador);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
