import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { RubroDTO, UsuarioRubroDTO } from '../../Modelos/rubro.model';
import { RubroServicio } from '../../ServiciosCompartidos/rubro-servicio';
import { RubroSocioServicio } from '../../ServiciosCompartidos/rubro-socio-servicio';
import { UsuarioRubroServicio } from '../../ServiciosCompartidos/usuario-rubro-servicio';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { HeraderSocio } from "../herader-socio/herader-socio";
import { RubrosDisponibles } from '../rubros-disponibles/rubros-disponibles';

@Component({
  selector: 'app-rubros-socio',
  standalone: true,
  imports: [CommonModule, FormsModule, HeraderSocio],
  templateUrl: './rubros-socio.html',
  styleUrl: './rubros-socio.css'
})
export class RubrosSocio implements OnInit {
  // Inyecciones
  private rubroServicio = inject(RubroServicio);
  private rubroSocioServicio = inject(RubroSocioServicio);
  private usuarioRubroServicio = inject(UsuarioRubroServicio);
  private authServicio = inject(AuthServicio);

  // Signals
  rubrosAsignados = signal<UsuarioRubroDTO[]>([]);
  rubrosDisponibles = signal<RubroDTO[]>([]);
  rubrosAsociadosSocio = signal<any[]>([]);
  loading = signal<boolean>(true);
  procesando = signal<boolean>(false);
  mensajeExito = signal<string>('');
  mensajeError = signal<string>('');
  mostrarFormularioClaveAcceso = signal<boolean>(false);
  claveAccesoIngresada = signal<string>('');
  usuarioId = signal<number>(0);
  socioId = signal<number>(0);

  ngOnInit(): void {
    this.obtenerIdUsuario();
    this.cargarDatos();
  }

  obtenerIdUsuario(): void {
    const userId = this.authServicio.getUserId();
    if (userId && userId > 0) {
      this.usuarioId.set(userId);
      // Aquí deberías obtener el socioId del usuario
      // Por ahora usa el usuarioId como referencia
      this.socioId.set(userId);
    }
  }

  cargarDatos(): void {
    this.loading.set(true);
    
    // Cargar rubros asignados del usuario
    this.usuarioRubroServicio.obtenerTodos().subscribe({
      next: (asignados) => {
        this.rubrosAsignados.set(asignados.filter(ua => ua.idUsuario === this.usuarioId()));
        this.obtenerRubrosAsociados();
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando rubros asignados:', err);
        this.loading.set(false);
      }
    });
  }

  obtenerRubrosAsociados(): void {
    if (this.socioId() > 0) {
      this.rubroSocioServicio.obtenerPorSocio(this.socioId()).subscribe({
        next: (rubros) => {
          this.rubrosAsociadosSocio.set(rubros);
        },
        error: (err) => {
          console.error('Error cargando rubros asociados:', err);
          this.rubrosAsociadosSocio.set([]);
        }
      });
    }
  }

  isRubroAsociado(rubroId: number): boolean {
    return this.rubrosAsociadosSocio().some(r => r.idRubro === rubroId);
  }

  obtenerNombreRubro(rubroId: number): string {
    return this.rubrosDisponibles().find(r => r.id === rubroId)?.nombre || 'N/A';
  }

  asociarConClaveAcceso(): void {
    if (!this.claveAccesoIngresada()) {
      this.mensajeError.set('Por favor ingresa la clave de acceso');
      setTimeout(() => this.mensajeError.set(''), 5000);
      return;
    }

    if (this.socioId() <= 0) {
      this.mensajeError.set('No se pudo determinar tu ID de socio');
      setTimeout(() => this.mensajeError.set(''), 5000);
      return;
    }

    this.procesando.set(true);
    this.rubroSocioServicio.asociarPorClaveAcceso(this.socioId(), this.claveAccesoIngresada()).subscribe({
      next: (resultado) => {
        this.mensajeExito.set('✅ ¡Te has asociado correctamente al rubro!');
        this.claveAccesoIngresada.set('');
        this.mostrarFormularioClaveAcceso.set(false);
        setTimeout(() => this.mensajeExito.set(''), 5000);
        this.cargarDatos();
        this.procesando.set(false);
      },
      error: (err) => {
        console.error('Error:', err);
        this.mensajeError.set(err.error?.message || 'Error al asociarse. Verifica la clave de acceso.');
        setTimeout(() => this.mensajeError.set(''), 5000);
        this.procesando.set(false);
      }
    });
  }

  desasociarse(rubroId: number): void {
    if (!confirm('¿Deseas desasociarte de este rubro?')) {
      return;
    }

    this.procesando.set(true);
    this.rubroSocioServicio.eliminarRelacion(rubroId, this.socioId()).subscribe({
      next: () => {
        this.mensajeExito.set('✅ Te has desasociado del rubro correctamente');
        setTimeout(() => this.mensajeExito.set(''), 5000);
        this.cargarDatos();
        this.procesando.set(false);
      },
      error: (err) => {
        console.error('Error:', err);
        this.mensajeError.set('Error al desasociarse del rubro');
        setTimeout(() => this.mensajeError.set(''), 5000);
        this.procesando.set(false);
      }
    });
  }
}
