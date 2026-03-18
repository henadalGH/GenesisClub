import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SolicitudRubroServicio, SolicitudRubroDTO } from '../../ServicioAdministrador/solicitud-rubro-servicio';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';

@Component({
  selector: 'app-solicitudes-rubros-pendientes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './solicitudes-rubros-pendientes.component.html',
  styleUrl: './solicitudes-rubros-pendientes.component.css'
})
export class SolicitudesRubrosPendientesComponent implements OnInit {

  solicitudes: SolicitudRubroDTO[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private solicitudService: SolicitudRubroServicio,
    private authService: AuthServicio
  ) {}

  ngOnInit(): void {
    console.log('=== DEBUG SOLICITUDES RUBROS ===');
    this.authService.debugToken();
    this.cargarSolicitudes();
  }

  cargarSolicitudes(): void {
    this.loading = true;
    this.solicitudService.obtenerSolicitudesPendientes().subscribe({
      next: (data) => {
        this.solicitudes = data;
        this.loading = false;
      },
      error: (error) => {
        if (error.status === 403) {
          this.errorMessage = '❌ No tienes permisos de ADMIN para acceder a esta sección. Rol actual: ' + this.authService.getRol();
        } else if (error.status === 401) {
          this.errorMessage = '❌ Tu sesión expiró. Por favor inicia sesión nuevamente.';
        } else {
          this.errorMessage = 'Error al cargar solicitudes: ' + (error.error?.message || error.message);
        }
        console.error('Error cargando solicitudes:', error);
        this.loading = false;
      }
    });
  }

  aprobar(id: number): void {
    this.solicitudService.aprobarSolicitud(id).subscribe({
      next: () => {
        alert('Solicitud aprobada.');
        this.cargarSolicitudes();
      },
      error: (error) => {
        const msg = error.error?.error || error.error?.message || 'Error al aprobar solicitud.';
        alert(msg);
      }
    });
  }

  rechazar(id: number): void {
    this.solicitudService.rechazarSolicitud(id).subscribe({
      next: () => {
        alert('Solicitud rechazada.');
        this.cargarSolicitudes();
      },
      error: (error) => {
        const msg = error.error?.error || error.error?.message || 'Error al rechazar solicitud.';
        alert(msg);
      }
    });
  }

}