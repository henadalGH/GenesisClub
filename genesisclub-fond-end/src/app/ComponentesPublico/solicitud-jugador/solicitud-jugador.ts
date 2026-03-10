import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { SolicitudServicio } from '../../ServiciosCompartidos/solicitud-servicio';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-solicitud-jugador',
  standalone: true,
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './solicitud-jugador.html',
  styleUrls: ['./solicitud-jugador.css'],
})
export class SolicitudJugador {
  nombre: string = '';
  apellido: string = '';
  email: string = '';
  contacto: string = '';
  password: string = '';
  confirmPassword: string = '';

  // campos de vehículo
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
    if (!this.email || !this.nombre || !this.apellido || !this.contacto || !this.password) {
      alert('Por favor, completa todos los campos.');
      return;
    }

    if (this.password !== this.confirmPassword) {
      alert('Las contraseñas no coinciden.');
      return;
    }

    this.cargando = true;

    const payload: any = {
      nombre: this.nombre,
      apellido: this.apellido,
      email: this.email,
      contacto: this.contacto,
      password: this.password,
      patente: this.patente || null,
      marca: this.marca || null,
      modelo: this.modelo || null,
      anio: this.anio || null,
      tieneGnc: this.tieneGnc
    };

    this.solicitudServicio.enviarSolicitudJugador(payload).subscribe({
      next: (res) => {
        alert(res.mensage);
        this.router.navigate(['/inicio']);
      },
      error: (err) => {
        this.cargando = false;
        const mensajeBack = err.error?.mensage || 'Error al procesar la solicitud';
        alert(mensajeBack);
        console.error('Error desde el servidor:', err);
      }
    });
  }
}