import { Component, OnInit } from '@angular/core';
import { HeaderAdmin } from "../header-admin/header-admin";
import { PendientesServicios } from '../../ServicioAdministrador/pendientes-servicios';

type Estado = 'ACEPTADA' | 'RECHAZADA';

@Component({
  selector: 'app-solicitudes-pendientes',
  templateUrl: './solicitudes-pendientes.html',
  styleUrls: ['./solicitudes-pendientes.css'],
  imports: [HeaderAdmin],
})
export class SolicitudesPendientes implements OnInit {

  pendiente: any[] = [];
  cargando = false;

  constructor(private pendientesServicios: PendientesServicios) {}

  ngOnInit(): void {
    this.verPendientes();
  }

  // =========================
  // CARGAR LISTA
  // =========================
  verPendientes() {
    this.pendientesServicios.listarPendientes().subscribe({
      next: (resp: any) => {
        this.pendiente = resp;
      }
    });
  }

  // =========================
  // CAMBIAR ESTADO
  // =========================
  cambiarEstado(id: number, estado: Estado) {

    this.cargando = true;

    this.pendientesServicios.modificaEstado(id, estado).subscribe({
      next: () => {

        // 🔥 QUITAMOS SOLO ESA CARD (sin refrescar todo)
        this.pendiente = this.pendiente.filter(p => p.id !== id);

        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        alert('Error al actualizar estado');
      }
    });
  }
}
