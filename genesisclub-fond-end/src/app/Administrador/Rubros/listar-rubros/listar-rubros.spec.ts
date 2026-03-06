import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListarRubros } from './listar-rubros';

describe('ListarRubros', () => {
  let component: ListarRubros;
  let fixture: ComponentFixture<ListarRubros>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarRubros]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListarRubros);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
