import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Pruevas } from './pruevas';

describe('Pruevas', () => {
  let component: Pruevas;
  let fixture: ComponentFixture<Pruevas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Pruevas]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Pruevas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
