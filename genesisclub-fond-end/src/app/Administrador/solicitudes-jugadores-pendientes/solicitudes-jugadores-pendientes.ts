import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { HeaderAdmin } from "../header-admin/header-admin";
import { JugadorPendientesServicios } from '../../ServicioAdministrador/jugador-pendientes-servicios';
import { NotificacionesServicio } from '../../ServiciosCompartidos/notificaciones-servicio';
import { ModalConfirmacionComponent } from '../../ComponentesCompartidos/modal-confirmacion/modal-confirmacion';
import { CommonModule } from '@angular/common';
import { SolicitudJugadorDTO } from '../../Modelos/usuario.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

type Estado = 'ACEPTADA' | 'RECHAZADA';

interface ModalState {
  visible: boolean;
  id?: number;
  accion?: 'aceptar' | 'rechazar';
}

@Component({
  selector: 'app-solicitudes-jugadores-pendientes',
  templateUrl: './solicitudes-jugadores-pendientes.html',
  styleUrl: './solicitudes-jugadores-pendientes.css',
  standalone: true,
  imports: [HeaderAdmin, CommonModule, ModalConfirmacionComponent],
})
export class SolicitudesJugadoresPendientes implements OnInit, OnDestroy {

  pendiente: SolicitudJugadorDTO[] = [];
  cargando = false;
  modalState: ModalState = { visible: false };
  private destroy$ = new Subject<void>();

  constructor(
    private jugadorPendientesServicios: JugadorPendientesServicios,
    private notificacionesServicio: NotificacionesServicio,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.verPendientes();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  verPendientes(): void {
    this.cargando = true;
    this.cdr.detectChanges();
    
    this.jugadorPendientesServicios.listarPendientes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (resp: SolicitudJugadorDTO[]) => {
          this.pendiente = resp || [];
          this.cargando = false;
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          this.cargando = false;
          this.cdr.detectChanges();
          
          // Manejo específico de timeout
          if (err.name === 'TimeoutError') {
            const mensaje = '❌ La petición tardó demasiado. ¿Está ejecutándose el backend?';
            this.notificacionesServicio.error(mensaje);
            console.error('Timeout:', err);
          } else {
            const mensaje = err?.message || 'Error al cargar solicitudes';
            this.notificacionesServicio.error(mensaje);
            console.error('Error al cargar pendientes:', err);
          }
        }
      });
  }

  abrirModalAceptar(id: number): void {
    this.modalState = {
      visible: true,
      id: id,
      accion: 'aceptar'
    };
  }

  abrirModalRechazar(id: number): void {
    this.modalState = {
      visible: true,
      id: id,
      accion: 'rechazar'
    };
  }

  cerrarModal(): void {
    this.modalState = { visible: false };
  }

  confirmarAccion(): void {
    if (!this.modalState.id || !this.modalState.accion) return;

    const id = this.modalState.id;
    const estado: Estado = this.modalState.accion === 'aceptar' ? 'ACEPTADA' : 'RECHAZADA';

    this.cambiarEstado(id, estado);
  }

  private cambiarEstado(id: number, estado: Estado): void {
    if (this.cargando) return;

    this.cargando = true;

    this.jugadorPendientesServicios.modificaEstado(id, estado)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.pendiente = this.pendiente.filter(p => p.id !== id);
          this.cargando = false;

          const mensaje = estado === 'ACEPTADA' 
            ? '✓ Solicitud aceptada exitosamente' 
            : '✓ Solicitud rechazada';
          this.notificacionesServicio.exito(mensaje);

          this.cerrarModal();
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.cargando = false;
          const mensaje = err?.message || 'Error al actualizar solicitud';
          this.notificacionesServicio.error(mensaje);
          console.error('Error al cambiar estado:', err);
          this.cdr.detectChanges();
        }
      });
  }

  getTextoModal(): { titulo: string; mensaje: string; texto: string } {
    if (this.modalState.accion === 'aceptar') {
      return {
        titulo: 'Aceptar Solicitud',
        mensaje: '¿Deseas aceptar esta solicitud de jugador?',
        texto: 'Aceptar'
      };
    }
    return {
      titulo: 'Rechazar Solicitud',
      mensaje: '¿Deseas rechazar esta solicitud de jugador?',
      texto: 'Rechazar'
    };
  }

  getTipoModal(): 'aceptar' | 'rechazar' {
    return this.modalState.accion === 'aceptar' ? 'aceptar' : 'rechazar';
  }
}
