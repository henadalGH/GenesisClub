import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerResPorSocio } from './ver-res-por-socio';

describe('VerResPorSocio', () => {
  let component: VerResPorSocio;
  let fixture: ComponentFixture<VerResPorSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerResPorSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerResPorSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
