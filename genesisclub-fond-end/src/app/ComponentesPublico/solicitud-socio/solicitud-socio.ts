import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { SolicitudServicio } from '../../ServiciosCompartidos/solicitud-servicio';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-solicitud-socio',
  standalone: true,
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './solicitud-socio.html',
  styleUrls: ['./solicitud-socio.css'],
})
export class SolicitudSocio {
  // Propiedades vinculadas al [(ngModel)] del HTML
  nombre: string = '';
  apellido: string = '';
  email: string = '';
  contacto: string = '';
  password: string = '';
  confirmPassword: string = '';
  // campos de vehículo opcionales
  patente: string = '';
  marca: string = '';
  modelo: string = '';
  anio?: number;
  tieneGnc: boolean = false;

  cargando: boolean = false;

  constructor(
    private solicitudServicio: SolicitudServicio,
    private router: Router
  ) { }

  enviarSolicitud() {
    // 1. Validaciones de campos vacíos
    if (!this.email || !this.nombre || !this.apellido || !this.contacto || !this.password) {
      alert('Por favor, completa todos los campos.');
      return;
    }

    // 2. Validación de contraseñas
    if (this.password !== this.confirmPassword) {
      alert('Las contraseñas no coinciden.');
      return;
    }

    this.cargando = true;

    // 3. Creamos el objeto para enviar (esto arregla el error de los 5 argumentos)
    const payload: any = {
      nombre: this.nombre,
      apellido: this.apellido,
      email: this.email,
      contacto: this.contacto,
      password: this.password
    };
    // añadimos datos de vehículo solo si hay patente
    if (this.patente) {
      payload.patente = this.patente;
      payload.marca = this.marca;
      payload.modelo = this.modelo;
      payload.anio = this.anio;
      payload.tieneGnc = this.tieneGnc;
    }

    // 4. Llamada al servicio con manejo de error 400
    this.solicitudServicio.enviarSolicitud(payload).subscribe({
      next: (res) => {
        alert(res.mensage); // "Solicitud creada correctamente"
        this.router.navigate(['/inicio']);
      },
      error: (err) => {
        this.cargando = false;
        // Capturamos el mensaje de "Email ya vinculado..." que enviamos desde Java
        const mensajeBack = err.error?.mensage || 'Error al procesar la solicitud';
        alert(mensajeBack);
        console.error('Error desde el servidor:', err);
      }
    });
  }
}