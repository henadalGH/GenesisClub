import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSocios } from './mis-socios';

describe('MisSocios', () => {
  let component: MisSocios;
  let fixture: ComponentFixture<MisSocios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisSocios]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MisSocios);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
