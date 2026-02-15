import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Invitaciones } from './invitaciones';

describe('Invitaciones', () => {
  let component: Invitaciones;
  let fixture: ComponentFixture<Invitaciones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Invitaciones]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Invitaciones);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
