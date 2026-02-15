import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InicioSocio } from './inicio-socio';

describe('InicioSocio', () => {
  let component: InicioSocio;
  let fixture: ComponentFixture<InicioSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InicioSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InicioSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
