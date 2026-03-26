import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntrarRubroSocio } from './entrar-rubro-socio';

describe('EntrarRubroSocio', () => {
  let component: EntrarRubroSocio;
  let fixture: ComponentFixture<EntrarRubroSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntrarRubroSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntrarRubroSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
