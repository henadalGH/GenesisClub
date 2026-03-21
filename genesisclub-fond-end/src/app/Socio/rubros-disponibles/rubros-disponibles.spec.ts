import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RubrosDisponibles } from './rubros-disponibles';

describe('RubrosDisponibles', () => {
  let component: RubrosDisponibles;
  let fixture: ComponentFixture<RubrosDisponibles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RubrosDisponibles]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RubrosDisponibles);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
