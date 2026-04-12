import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderJugador } from './header-jugador';

describe('HeaderJugador', () => {
  let component: HeaderJugador;
  let fixture: ComponentFixture<HeaderJugador>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderJugador]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderJugador);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
