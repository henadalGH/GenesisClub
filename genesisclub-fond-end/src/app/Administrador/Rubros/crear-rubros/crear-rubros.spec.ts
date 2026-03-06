import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearRubros } from './crear-rubros';

describe('CrearRubros', () => {
  let component: CrearRubros;
  let fixture: ComponentFixture<CrearRubros>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrearRubros]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrearRubros);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
