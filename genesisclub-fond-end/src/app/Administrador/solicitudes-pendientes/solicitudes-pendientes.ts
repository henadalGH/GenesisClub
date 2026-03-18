import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HeaderAdmin } from "../header-admin/header-admin";
import { PendientesServicios } from '../../ServicioAdministrador/pendientes-servicios';
import { CommonModule } from '@angular/common';

type Estado = 'ACEPTADA' | 'RECHAZADA';

@Component({
  selector: 'app-solicitudes-pendientes',
  templateUrl: './solicitudes-pendientes.html',
  styleUrl: './solicitudes-pendientes.css',
  standalone: true,
  imports: [HeaderAdmin, CommonModule],
})
export class SolicitudesPendientes implements OnInit {

  pendiente: any[] = [];
  cargando = false;

  constructor(
    private pendientesServicios: PendientesServicios,
    private cdr: ChangeDetectorRef // 1. Inyectamos el detector de cambios
  ) {}

  ngOnInit(): void {
    this.verPendientes();
  }

  // =========================
  // CARGAR LISTA
  // =========================
  verPendientes() {
    this.cargando = true;
    this.pendientesServicios.listarPendientes().subscribe({
      next: (resp: any) => {
        console.log('Respuesta del servidor:', resp);
        // 2. Usamos el spread operator para asegurar que Angular detecte el nuevo array
        this.pendiente = [...resp];
        this.cargando = false;
        
        // 3. Forzamos la actualización de la vista
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        console.error('Error al cargar pendientes:', err);
      }
    });
  }

  // =========================
  // CAMBIAR ESTADO
  // =========================
  cambiarEstado(id: number, estado: Estado) {
    if (this.cargando) return; // Evita múltiples clics accidentales

    this.cargando = true;

    this.pendientesServicios.modificaEstado(id, estado).subscribe({
      next: () => {
        // 4. FILTRADO: Creamos un NUEVO array excluyendo el ID procesado
        // Esto es lo que hace que la card desaparezca de inmediato
        this.pendiente = this.pendiente.filter(p => p.id !== id);

        this.cargando = false;
        
        // 5. Notificamos a Angular que la lista cambió
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