import { Component, OnInit, signal, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RubroDTO, UsuarioRubroDTO } from '../../Modelos/rubro.model';
import { RubroServicio } from '../../ServiciosCompartidos/rubro-servicio';
import { UsuarioRubroServicio } from '../../ServiciosCompartidos/usuario-rubro-servicio';
import { SolicitudRubroServicio, CrearSolicitudRubroDTO } from '../../ServicioAdministrador/solicitud-rubro-servicio';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { HeraderSocio } from "../herader-socio/herader-socio";
import { ModalSolicitudRubroComponent } from '../../ComponentesCompartidos/modal-solicitud-rubro/modal-solicitud-rubro';

@Component({
  selector: 'app-rubros-disponibles',
  standalone: true,
  imports: [CommonModule, HeraderSocio, ModalSolicitudRubroComponent],
  templateUrl: './rubros-disponibles.html',
  styleUrl: './rubros-disponibles.css',
})
export class RubrosDisponibles implements OnInit {
  // Inyecciones
  private rubroServicio = inject(RubroServicio);
  private usuarioRubroServicio = inject(UsuarioRubroServicio);
  private solicitudRubroServicio = inject(SolicitudRubroServicio);
  private authServicio = inject(AuthServicio);
  private router = inject(Router);

  // Inputs
  @Input() usuarioId!: any;
  @Input() rubrosAsignados!: any;
  @Input() rubrosAsociadosSocio!: any;

  // Signals
  rubrosDisponibles = signal<RubroDTO[]>([]);
  loading = signal<boolean>(true);
  procesando = signal<boolean>(false);
  mensajeExito = signal<string>('');
  mensajeError = signal<string>('');

  // Signals para el modal
  modalVisible = signal<boolean>(false);
  rubroSeleccionado = signal<RubroDTO | null>(null);
  procesandoModal = signal<boolean>(false);
  mensajeErrorModal = signal<string>('');

  ngOnInit(): void {
    this.cargarRubrosDisponibles();
  }

  cargarRubrosDisponibles(): void {
    this.loading.set(true);
    this.rubroServicio.obtenerActivos().subscribe({
      next: (rubros) => {
        this.rubrosDisponibles.set(rubros);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando rubros disponibles:', err);
        this.loading.set(false);
      }
    });
  }

  isRubroAsignado(rubroId: number): boolean {
    try {
      const asignados = typeof this.rubrosAsignados === 'function' ? this.rubrosAsignados() : this.rubrosAsignados || [];
      return Array.isArray(asignados) ? asignados.some((ua: any) => ua?.idRubro === rubroId) : false;
    } catch (e) {
      return false;
    }
  }

  isRubroAsociado(rubroId: number): boolean {
    try {
      const asociados = typeof this.rubrosAsociadosSocio === 'function' ? this.rubrosAsociadosSocio() : this.rubrosAsociadosSocio || [];
      return Array.isArray(asociados) ? asociados.some((r: any) => r?.idRubro === rubroId) : false;
    } catch (e) {
      return false;
    }
  }

  obtenerNombreRubro(rubroId: number): string {
    return this.rubrosDisponibles().find(r => r.id === rubroId)?.nombre || 'N/A';
  }

  hayRubrosDisponibles(): boolean {
    return this.rubrosDisponibles().some(r => !this.isRubroAsignado(r.id) && !this.isRubroAsociado(r.id));
  }

  solicitarRubro(rubro: RubroDTO): void {
    if (!rubro || !rubro.id) {
      this.mensajeError.set('No se pudo determinar el ID del rubro. Por favor recargá la página y volvé a intentarlo.');
      return;
    }

    this.rubroSeleccionado.set(rubro);
    this.mensajeErrorModal.set('');
    this.modalVisible.set(true);
  }

  confirmarSolicitudRubro(claveAcceso: string): void {
    const rubro = this.rubroSeleccionado();
    if (!rubro || !rubro.id) {
      this.mensajeErrorModal.set('Rubro no válido. Por favor intentá de nuevo.');
      return;
    }

    if (!claveAcceso || claveAcceso.trim().length === 0) {
      this.mensajeErrorModal.set('Por favor ingresá la clave de acceso.');
      return;
    }

    this.procesandoModal.set(true);
    this.mensajeErrorModal.set('');

    try {
      const userId = this.authServicio.getUserId();
      if (!userId) {
        throw new Error('Usuario no autenticado');
      }

      const dto: CrearSolicitudRubroDTO = {
        socioId: userId,
        rubroId: rubro.id,
        claveAcceso: claveAcceso
      };

      this.solicitudRubroServicio.crearSolicitud(dto).subscribe({
        next: (response) => {
          this.procesandoModal.set(false);
          this.cerrarModal();
          this.mensajeExito.set(`¡Solicitud enviada correctamente para ${rubro.nombre}!`);
          // Limpiar el mensaje de éxito después de 5 segundos
          setTimeout(() => this.mensajeExito.set(''), 5000);
        },
        error: (error) => {
          this.procesandoModal.set(false);
          console.error('Error al solicitar rubro:', error);
          this.mensajeErrorModal.set(
            error.error?.error ||
            error.error?.message ||
            'Error al enviar la solicitud. Por favor intentá de nuevo.'
          );
        }
      });
    } catch (error: any) {
      this.procesandoModal.set(false);
      this.mensajeErrorModal.set(error.message || 'Error al procesar la solicitud.');
    }
  }

  cerrarModal(): void {
    this.modalVisible.set(false);
    this.rubroSeleccionado.set(null);
    this.mensajeErrorModal.set('');
  }
}
