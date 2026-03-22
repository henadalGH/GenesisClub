import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SolicitudRubroServicio, CrearSolicitudRubroDTO } from '../../ServicioAdministrador/solicitud-rubro-servicio';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';

@Component({
  selector: 'app-solicitar-rubro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './solicitar-rubro.component.html',
  styleUrl: './solicitar-rubro.component.css'
})
export class SolicitarRubroComponent implements OnInit {

  rubroId: number | null = null;
  claveAcceso: string = '';
  loading = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private solicitudService: SolicitudRubroServicio,
    private authService: AuthServicio
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const parsedId = idParam ? Number(idParam) : NaN;

    if (!idParam || Number.isNaN(parsedId)) {
      this.errorMessage = 'ID de rubro inválido. Volvé a la lista de rubros disponibles e intentá de nuevo.';
      this.rubroId = null;
      return;
    }

    this.rubroId = parsedId;
  }

  solicitar(): void {
    if (!this.rubroId || !this.claveAcceso) {
      this.errorMessage = 'Por favor, complete todos los campos.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const dto: CrearSolicitudRubroDTO = {
      socioId: this.getCurrentUserId(), // TODO: Obtener ID del usuario actual
      rubroId: this.rubroId,
      claveAcceso: this.claveAcceso
    };

    this.solicitudService.crearSolicitud(dto).subscribe({
      next: (response) => {
        this.loading = false;
        alert('Solicitud enviada exitosamente.');
        this.router.navigate(['/rubrosDisponibles']);
      },
      error: (error) => {
        this.loading = false;
        console.error('Solicitud Rubro error:', error);
        this.errorMessage =
          error.error?.error ||
          error.error?.message ||
          'Error al enviar la solicitud.';
      }
    });
  }

  private getCurrentUserId(): number {
    const socioId = this.authService.getSocioId();
    if (!socioId) {
      throw new Error('Usuario (socio) no autenticado o sin socioId');
    }
    return socioId;
  }

}