import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { SolicitudServicio } from '../../ServiciosCompartidos/solicitud-servicio';

@Component({
  selector: 'app-solicitud-socio',
  imports: [RouterLink, FormsModule],
  templateUrl: './solicitud-socio.html',
  styleUrls: ['./solicitud-socio.css'], // corregido de styleUrl a styleUrls
})
export class SolicitudSocio {

  constructor(
    private solicitudServicio: SolicitudServicio,
    private router: Router
  ) { }

  nombre: string = '';
  apellido: string = '';
  email: string = '';
  contacto: string = '';

  // Nuevos campos para contraseña
  password: string = '';
  confirmPassword: string = '';

  enviarSolicitud() {
    // Validación de campos obligatorios
    if (!this.email || !this.nombre || !this.apellido || !this.contacto || !this.password || !this.confirmPassword) {
      alert('Complete todos los campos');
      return;
    }

    // Validación de coincidencia de contraseñas
    if (this.password !== this.confirmPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }

    // Enviar solicitud al servicio
    this.solicitudServicio.enviarSolicitud(
      this.nombre,
      this.apellido,
      this.email,
      this.contacto,
      this.password // enviamos la contraseña al backend
    ).subscribe(
      (responce) => {
        alert('Solicitud enviada con éxito');
        this.router.navigate(['/inicio']);
      },
      (error) => {
        alert('Error al enviar la solicitud');
        console.error(error);
      }
    );
  }

}
