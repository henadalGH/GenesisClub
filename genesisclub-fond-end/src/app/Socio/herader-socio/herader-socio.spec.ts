import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeraderSocio } from './herader-socio';

describe('HeraderSocio', () => {
  let component: HeraderSocio;
  let fixture: ComponentFixture<HeraderSocio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeraderSocio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeraderSocio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
