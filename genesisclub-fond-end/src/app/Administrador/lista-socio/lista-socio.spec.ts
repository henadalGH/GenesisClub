import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaSocio } from './lista-socio';

describe('ListaSocio', () => {
  let component: ListaSocio;
  let fixture: ComponentFixture<ListaSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListaSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
