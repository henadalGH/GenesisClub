import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { SolicitudServicio } from '../../ServiciosCompartidos/solicitud-servicio';
import { SolicitudSocioDTO } from '../../Modelos/usuario.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-solicitud-socio',
  standalone: true,
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './solicitud-socio.html',
  styleUrl: './solicitud-socio.css',
})
export class SolicitudSocio {

  nombre: string = '';
  apellido: string = '';
  email: string = '';
  codigoArea: string = '';
  numeroCelular: string = '';
  password: string = '';
  confirmPassword: string = '';
  patente: string = '';
  marca: string = '';
  modelo: string = '';
  anio: number | null = null;
  tieneGnc: boolean = false;

  mostrarPassword: boolean = false;
  mostrarConfirmPassword: boolean = false;

  cargando: boolean = false;
  errores: any = {};

  constructor(
    private solicitudServicio: SolicitudServicio,
    private router: Router
  ) {}

  validarCampo(control: any, campo: string): void {
    if (control.invalid) {
      if (control.errors?.['required']) {
        this.errores[campo] = 'Este campo es obligatorio';
      } else if (control.errors?.['pattern']) {
        switch (campo) {
          case 'password':
            this.errores[campo] = 'La contraseña debe tener mínimo 8 caracteres, mayúscula, minúscula, número y símbolo';
            break;
          case 'codigoArea':
            this.errores[campo] = 'Código de área inválido (solo números)';
            break;
          case 'numeroCelular':
            this.errores[campo] = 'Número de celular inválido (solo números)';
            break;
          case 'email':
            this.errores[campo] = 'Ingresá un correo válido';
            break;
          default:
            this.errores[campo] = 'Formato inválido';
        }
      }
    } else {
      this.errores[campo] = '';
    }
  }

  validarConfirmPassword(): void {
    if (this.confirmPassword && this.confirmPassword !== this.password) {
      this.errores.confirmPassword = 'Las contraseñas no coinciden';
    } else {
      this.errores.confirmPassword = '';
    }
  }

  validarPatente(): void {
    if (!this.patente) {
      this.errores.patente = '';
      return;
    }

    const regexVieja = /^[A-Z]{3}[- ]?\d{3}$/i;
    const regexNueva = /^[A-Z]{2}\d{3}[A-Z]{2}$/i;

    if (!regexVieja.test(this.patente) && !regexNueva.test(this.patente)) {
      this.errores.patente = 'Patente inválida para Argentina';
    } else {
      this.errores.patente = '';
    }
  }

  enviarSolicitud(): void {
    if (!this.nombre || !this.apellido || !this.email || !this.codigoArea || !this.numeroCelular || !this.password) {
      alert('Por favor, completa todos los campos obligatorios.');
      return;
    }

    if (this.password !== this.confirmPassword) {
      alert('Las contraseñas no coinciden.');
      return;
    }

    if (this.errores.patente) {
      alert('Corrige la patente antes de enviar.');
      return;
    }

    this.cargando = true;

    const payload = {
      nombre: this.nombre,
      apellido: this.apellido,
      email: this.email,
      codigoArea: this.codigoArea,
      numeroCelular: this.numeroCelular,
      patente: this.patente || undefined,
      marca: this.marca || undefined,
      modelo: this.modelo || undefined,
      anio: this.anio || undefined,
      tieneGnc: this.tieneGnc
    };

    this.solicitudServicio.enviarSolicitud(payload).subscribe({
      next: (res) => {
        this.cargando = false;
        alert(res.mensage);
        this.router.navigate(['/inicio']);
      },
      error: (err) => {
        this.cargando = false;
        const mensajeBack = err?.error?.mensage ?? 'Error al procesar la solicitud';
        alert(mensajeBack);
        console.error('Error desde el servidor:', err);
      }
    });
  }
}
