import { Component, OnInit } from '@angular/core';
import { HeaderAdmin } from "../header-admin/header-admin";
import { SocioServicio } from '../../ServicioAdministrador/socio-servicio';
import { Router } from '@angular/router';

@Component({
  selector: 'app-lista-socio',
  standalone: true,
  imports: [HeaderAdmin],
  templateUrl: './lista-socio.html',
  styleUrl: './lista-socio.css',
})
export class ListaSocio implements OnInit {

  socios: any[] = [];

  constructor(
    private socioServicio: SocioServicio,
    private ruter: Router
  ) {}

  ngOnInit() {
    this.obtenerSocio(); // ← ACA SE LLAMA
  }

  obtenerSocio() {
    this.socioServicio.obtenerSocio().subscribe(
      (respuesta: any) => {
        this.socios = respuesta;
      }
    );
  }
}
