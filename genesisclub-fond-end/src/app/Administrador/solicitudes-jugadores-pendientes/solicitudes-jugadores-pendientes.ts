import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HeaderAdmin } from "../header-admin/header-admin";
import { JugadorPendientesServicios } from '../../ServicioAdministrador/jugador-pendientes-servicios';
import { CommonModule } from '@angular/common';

type Estado = 'ACEPTADA' | 'RECHAZADA';

@Component({
  selector: 'app-solicitudes-jugadores-pendientes',
  templateUrl: './solicitudes-jugadores-pendientes.html',
  styleUrls: ['./solicitudes-jugadores-pendientes.css'],
  standalone: true,
  imports: [HeaderAdmin, CommonModule],
})
export class SolicitudesJugadoresPendientes implements OnInit {

  pendiente: any[] = [];
  cargando = false;

  constructor(
    private jugadorPendientesServicios: JugadorPendientesServicios,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.verPendientes();
  }

  verPendientes() {
    this.cargando = true;
    this.jugadorPendientesServicios.listarPendientes().subscribe({
      next: (resp: any) => {
        this.pendiente = [...resp];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error al cargar pendientes:', err);
      }
    });
  }

  cambiarEstado(id: number, estado: Estado) {
    if (this.cargando) return;

    this.cargando = true;

    this.jugadorPendientesServicios.modificaEstado(id, estado).subscribe({
      next: () => {
        this.pendiente = this.pendiente.filter(p => p.id !== id);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        alert('Error al actualizar estado');
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }
}
