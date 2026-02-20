import { Component, OnInit } from '@angular/core';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { RouterLink } from "@angular/router";
import { InvitacionServicio } from '../../ServicioAdministrador/invitacion-servicio';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-herader-socio',
  imports: [RouterLink, FormsModule],
  templateUrl: './herader-socio.html',
  styleUrl: './herader-socio.css',
})
export class HeraderSocio implements OnInit{


  modalAbierto = false;   // 👈 estado del popup

  constructor(private authServicio: AuthServicio,
    private invitacionServicio: InvitacionServicio
  ) {}


  ngOnInit(): void {
    const id = this.authServicio.getUserId();
  console.log('ID usuario:', id);
  }

  logout() {
    this.authServicio.logout();
  }

  abrirModal() {
    this.modalAbierto = true;
  }

  cerrarModal() {
    this.modalAbierto = false;
  }


    enviarInvitacion() {
      throw new Error('Method not implemented.');
    }
}
