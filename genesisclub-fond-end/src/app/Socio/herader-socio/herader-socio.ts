import { Component } from '@angular/core';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-herader-socio',
  imports: [RouterLink],
  templateUrl: './herader-socio.html',
  styleUrl: './herader-socio.css',
})
export class HeraderSocio {

  modalAbierto = false;   // 👈 estado del popup

  constructor(private authServicio: AuthServicio) {}

  logout() {
    this.authServicio.logout();
  }

  abrirModal() {
    this.modalAbierto = true;
  }

  cerrarModal() {
    this.modalAbierto = false;
  }
}
