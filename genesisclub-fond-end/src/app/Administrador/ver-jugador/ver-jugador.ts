import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { JugadorServicio } from '../../ServicioAdministrador/jugador-servicio';
import { HeaderAdmin } from '../header-admin/header-admin';

@Component({
  selector: 'app-ver-jugador',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderAdmin],
  templateUrl: './ver-jugador.html',
  styleUrl: './ver-jugador.css',
})
export class VerJugador implements OnInit {
  private jugadorServicio = inject(JugadorServicio);
  private route = inject(ActivatedRoute);

  jugador = signal<any>(null);
  vehiculos = signal<any[]>([]);
  cargando = signal<boolean>(true);
  procesando = signal<boolean>(false);
  id = signal<number>(0);

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id.set(Number(idParam));
      this.obtenerDatosJugador();
    }
  }
 
  obtenerDatosJugador() {
    this.cargando.set(true);
    this.jugadorServicio.obtenerJugadorPorId(this.id()).subscribe({
      next: (data) => {
        this.jugador.set(data);
        this.obtenerVehiculos();
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar jugador:', error);
        this.cargando.set(false);
      }
    });
  }

  obtenerVehiculos() {
    this.jugadorServicio.obtenerVehiculosJugador(this.id()).subscribe({
      next: (data) => {
        this.vehiculos.set(data);
      },
      error: (error) => {
        console.error('Error al cargar vehículos:', error);
        this.vehiculos.set([]);
      }
    });
  }

  suspenderJugador() {
    if (!confirm('¿Estás seguro de que deseas suspender este jugador?')) {
      return;
    }
    this.procesando.set(true);
    this.jugadorServicio.suspenderJugador(this.id()).subscribe({
      next: () => {
        alert('Jugador suspendido correctamente');
        this.obtenerDatosJugador();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al suspender jugador:', error);
        alert('Error al suspender jugador');
        this.procesando.set(false);
      }
    });
  }

  bloquearJugador() {
    if (!confirm('¿Estás seguro de que deseas bloquear este jugador?')) {
      return;
    }
    this.procesando.set(true);
    this.jugadorServicio.bloquearJugador(this.id()).subscribe({
      next: () => {
        alert('Jugador bloqueado correctamente');
        this.obtenerDatosJugador();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al bloquear jugador:', error);
        alert('Error al bloquear jugador');
        this.procesando.set(false);
      }
    });
  }

  activarJugador() {
    if (!confirm('¿Estás seguro de que deseas activar este jugador?')) {
      return;
    }
    this.procesando.set(true);
    this.jugadorServicio.activarJugador(this.id()).subscribe({
      next: () => {
        alert('Jugador activado correctamente');
        this.obtenerDatosJugador();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al activar jugador:', error);
        alert('Error al activar jugador');
        this.procesando.set(false);
      }
    });
  }
}
