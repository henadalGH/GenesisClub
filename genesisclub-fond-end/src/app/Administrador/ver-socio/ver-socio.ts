import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SocioServicio } from '../../ServicioAdministrador/socio-servicio';
import { HeaderAdmin } from '../header-admin/header-admin';

@Component({
  selector: 'app-ver-socio',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderAdmin],
  templateUrl: './ver-socio.html',
  styleUrls: ['./ver-socio.css'], // <-- Corregido
})
export class VerSocio implements OnInit {
  // Inyecciones modernas
  private socioServicio = inject(SocioServicio);
  private route = inject(ActivatedRoute);

  // Estados reactivos con Signals
  socio = signal<any>(null);
  vehiculos = signal<any[]>([]);
  cargando = signal<boolean>(true);
  procesando = signal<boolean>(false);
  idProc = signal<number>(0);
  id = signal<number>(0);

  ngOnInit(): void {
    // Obtenemos el ID de la URL
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id.set(Number(idParam));
      this.obtenerDatosSocio();
    }
  }

  obtenerDatosSocio() {
    this.cargando.set(true);
    this.socioServicio.verSocio(this.id()).subscribe({
      next: (data) => {
        this.socio.set(data);
        this.obtenerVehiculos();
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar socio:', error);
        this.cargando.set(false);
      }
    });
  }

  obtenerVehiculos() {
    this.socioServicio.obtenerVehiculosSocio(this.id()).subscribe({
      next: (data) => {
        this.vehiculos.set(data);
      },
      error: (error) => {
        console.error('Error al cargar vehículos:', error);
        this.vehiculos.set([]);
      }
    });
  }

  suspenderSocio() {
    if (!confirm('¿Estás seguro de que deseas suspender este socio?')) {
      return;
    }
    this.procesando.set(true);
    this.socioServicio.suspenderSocio(this.id()).subscribe({
      next: () => {
        alert('Socio suspendido correctamente');
        this.obtenerDatosSocio();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al suspender socio:', error);
        alert('Error al suspender socio');
        this.procesando.set(false);
      }
    });
  }

  bloquearSocio() {
    if (!confirm('¿Estás seguro de que deseas bloquear este socio?')) {
      return;
    }
    this.procesando.set(true);
    this.socioServicio.bloquearSocio(this.id()).subscribe({
      next: () => {
        alert('Socio bloqueado correctamente');
        this.obtenerDatosSocio();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al bloquear socio:', error);
        alert('Error al bloquear socio');
        this.procesando.set(false);
      }
    });
  }

  activarSocio() {
    if (!confirm('¿Estás seguro de que deseas activar este socio?')) {
      return;
    }
    this.procesando.set(true);
    this.socioServicio.activarSocio(this.id()).subscribe({
      next: () => {
        alert('Socio activado correctamente');
        this.obtenerDatosSocio();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al activar socio:', error);
        alert('Error al activar socio');
        this.procesando.set(false);
      }
    });
  }
}