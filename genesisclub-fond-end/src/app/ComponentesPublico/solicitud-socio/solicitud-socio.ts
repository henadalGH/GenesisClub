import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { SolicitudServicio } from '../../ServiciosCompartidos/solicitud-servicio';

@Component({
  selector: 'app-solicitud-socio',
  imports: [RouterLink, FormsModule],
  templateUrl: './solicitud-socio.html',
  styleUrl: './solicitud-socio.css',
})
export class SolicitudSocio {

  constructor(
    private solicitudServicio: SolicitudServicio,
    private router: Router
  ){

  }

  nombre: string ='';
  apellido: string = '';
  email: string = '';
  contacto: string = '';

  enviarSolicitud() {
    if (!this.email || !this.nombre || !this.apellido || !this.contacto ) {
      alert('Complete los campos');
      return;
    }

    this.solicitudServicio.enviarSolicitud(this.nombre, this.apellido,this.email, this.contacto).subscribe(
      (responce) => {
        this.router.navigate(['/inicio']);
      }
    )
  }

}


