import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SolicitudRubroServicio, SolicitudRubroDTO } from '../../ServicioAdministrador/solicitud-rubro-servicio';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { NotificacionesServicio } from '../../ServiciosCompartidos/notificaciones-servicio';
import { ModalConfirmacionComponent } from '../../ComponentesCompartidos/modal-confirmacion/modal-confirmacion';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HeaderAdmin } from "../header-admin/header-admin";

interface ModalState {
  visible: boolean;
  id?: number;
  accion?: 'aprobar' | 'rechazar';
}

@Component({
  selector: 'app-solicitudes-rubros-pendientes',
  standalone: true,
  imports: [CommonModule, ModalConfirmacionComponent, HeaderAdmin],
  templateUrl: './solicitudes-rubros-pendientes.component.html',
  styleUrl: './solicitudes-rubros-pendientes.component.css'
})
export class SolicitudesRubrosPendientesComponent implements OnInit, OnDestroy {

  solicitudes: SolicitudRubroDTO[] = [];
  loading = false;
  errorMessage = '';
  modalState: ModalState = { visible: false };
  private destroy$ = new Subject<void>();

  constructor(
    private solicitudService: SolicitudRubroServicio,
    private authService: AuthServicio,
    private notificacionesServicio: NotificacionesServicio,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('=== DEBUG SOLICITUDES RUBROS ===');
    this.authService.debugToken();
    this.cargarSolicitudes();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  cargarSolicitudes(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();
    
    console.log('Iniciando carga de solicitudes pendientes...');
    this.solicitudService.obtenerSolicitudesPendientes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          console.log('✅ Solicitudes cargadas:', data);
          this.solicitudes = data || [];
          this.loading = false;
          this.cdr.detectChanges();

          if (this.solicitudes.length === 0) {
            this.notificacionesServicio.info('✅ No hay solicitudes pendientes');
          }
        },
        error: (error) => {
          console.error('❌ Error completo:', error);
          this.loading = false;
          this.cdr.detectChanges();
          
          // Verificar si es error de timeout
          if (error.name === 'TimeoutError') {
            this.errorMessage = '❌ La petición tardó demasiado. ¿Está ejecutándose el backend en puerto 8080?';
            this.notificacionesServicio.error('Timeout - Backend no responde');
          }
          // Verificar si es error de conexión
          else if (!error.status || error.status === 0) {
            this.errorMessage = '❌ No se puede conectar al servidor. ¿Está ejecutándose el backend?';
            this.notificacionesServicio.error('No se puede conectar al servidor');
          } else if (error.status === 403) {
            this.errorMessage = '❌ No tienes permisos de ADMIN. Rol actual: ' + this.authService.getRol();
            this.notificacionesServicio.error('No tienes permisos para ver esto');
          } else if (error.status === 401) {
            this.errorMessage = '❌ Tu sesión expiró. Por favor inicia sesión nuevamente.';
            this.notificacionesServicio.error('Sesión expirada');
          } else {
            this.errorMessage = `❌ Error ${error.status}: ${error.error?.message || error.message || 'Error desconocido'}`;
            this.notificacionesServicio.error(this.errorMessage);
          }
        }
      });
  }

  abrirModalAprobar(id: number): void {
    this.modalState = {
      visible: true,
      id: id,
      accion: 'aprobar'
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
    const accion = this.modalState.accion;

    if (accion === 'aprobar') {
      this.aprobar(id);
    } else {
      this.rechazar(id);
    }
  }

  private aprobar(id: number): void {
    this.loading = true;
    this.cdr.detectChanges();
    
    this.solicitudService.aprobarSolicitud(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificacionesServicio.exito('✓ Solicitud aprobada exitosamente');
          this.solicitudes = this.solicitudes.filter(s => s.id !== id);
          this.loading = false;
          this.cdr.detectChanges();
          this.cerrarModal();
        },
        error: (error) => {
          this.loading = false;
          this.cdr.detectChanges();
          const msg = error.message || 'Error al aprobar solicitud';
          this.notificacionesServicio.error(msg);
          console.error('Error:', error);
        }
      });
  }

  private rechazar(id: number): void {
    this.loading = true;
    this.cdr.detectChanges();
    
    this.solicitudService.rechazarSolicitud(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificacionesServicio.exito('✓ Solicitud rechazada');
          this.solicitudes = this.solicitudes.filter(s => s.id !== id);
          this.loading = false;
          this.cdr.detectChanges();
          this.cerrarModal();
        },
        error: (error) => {
          this.loading = false;
          this.cdr.detectChanges();
          const msg = error.message || 'Error al rechazar solicitud';
          this.notificacionesServicio.error(msg);
          console.error('Error:', error);
        }
      });
  }

  getTextoModal(): { titulo: string; mensaje: string; texto: string } {
    if (this.modalState.accion === 'aprobar') {
      return {
        titulo: 'Aprobar Solicitud de Rubro',
        mensaje: '¿Deseas aprobar el acceso a este rubro?',
        texto: 'Aprobar'
      };
    }
    return {
      titulo: 'Rechazar Solicitud de Rubro',
      mensaje: '¿Deseas rechazar el acceso a este rubro?',
      texto: 'Rechazar'
    };
  }

  getTipoModal(): 'aceptar' | 'rechazar' {
    return this.modalState.accion === 'aprobar' ? 'aceptar' : 'rechazar';
  }
}