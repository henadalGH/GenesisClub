import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InicioRubros } from './inicio-rubros';

describe('InicioRubros', () => {
  let component: InicioRubros;
  let fixture: ComponentFixture<InicioRubros>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InicioRubros]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InicioRubros);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
