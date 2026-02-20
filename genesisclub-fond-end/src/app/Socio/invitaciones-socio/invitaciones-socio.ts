import { Component } from '@angular/core';
import { InvitacionServicio } from '../../ServicioAdministrador/invitacion-servicio';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-invitaciones-socio',
  imports: [FormsModule],
  templateUrl: './invitaciones-socio.html',
  styleUrl: './invitaciones-socio.css',
})
export class InvitacionesSocio {
enviarInvitacion() {
throw new Error('Method not implemented.');
}

  constructor(
    private invitacionServicio: InvitacionServicio
  ){}
}
