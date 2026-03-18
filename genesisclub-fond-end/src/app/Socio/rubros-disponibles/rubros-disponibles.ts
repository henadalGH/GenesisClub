import { Component, OnInit, signal, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RubroDTO, UsuarioRubroDTO } from '../../Modelos/rubro.model';
import { RubroServicio } from '../../ServiciosCompartidos/rubro-servicio';
import { UsuarioRubroServicio } from '../../ServiciosCompartidos/usuario-rubro-servicio';
import { HeraderSocio } from "../herader-socio/herader-socio";

@Component({
  selector: 'app-rubros-disponibles',
  standalone: true,
  imports: [CommonModule, HeraderSocio],
  templateUrl: './rubros-disponibles.html',
  styleUrl: './rubros-disponibles.css',
})
export class RubrosDisponibles implements OnInit {
  // Inyecciones
  private rubroServicio = inject(RubroServicio);
  private usuarioRubroServicio = inject(UsuarioRubroServicio);
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

  solicitarRubro(rubro: any): void {
    // Aceptamos tanto objetos RubroDTO (id) como objetos que vengan con idRubro
    const id = rubro?.id ?? rubro?.idRubro ?? rubro?.rubroId;

    if (!id || Number.isNaN(Number(id))) {
      this.mensajeError.set('No se pudo determinar el ID del rubro. Por favor recargá la página y volvé a intentarlo.');
      return;
    }

    this.router.navigate(['/solicitarRubro', id]);
  }
}
