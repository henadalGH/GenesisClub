import { ChangeDetectorRef, Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RubroSocioServicio } from '../../../ServiciosCompartidos/rubro-socio-servicio';
import { HeraderSocio } from "../../herader-socio/herader-socio";

@Component({
  selector: 'app-entrar-rubro-socio',
  imports: [HeraderSocio],
  templateUrl: './entrar-rubro-socio.html',
  styleUrl: './entrar-rubro-socio.css',
})
export class EntrarRubroSocio implements OnInit{

  rubro: any = null;
  id!: number;


  constructor(
    private rubroService: RubroSocioServicio,
    private router: ActivatedRoute,
    private cd: ChangeDetectorRef
  ){  }
  
  ngOnInit(): void {
    this.id = Number(this.router.snapshot.paramMap.get('id'));

    this.rubroService.obtenerPorId(this.id).subscribe(
      data => {
        this.rubro = data;
        this.cd.detectChanges();
      });
  }


  
}
