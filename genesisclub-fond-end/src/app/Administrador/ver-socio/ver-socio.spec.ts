import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerSocio } from './ver-socio';

describe('VerSocio', () => {
  let component: VerSocio;
  let fixture: ComponentFixture<VerSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
