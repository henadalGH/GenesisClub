import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SocioServicio } from '../../ServicioAdministrador/socio-servicio';
import { RubroServicio } from '../../ServiciosCompartidos/rubro-servicio';
import { RubroSocioServicio } from '../../ServiciosCompartidos/rubro-socio-servicio';
import { HeaderAdmin } from '../header-admin/header-admin';

@Component({
  selector: 'app-ver-socio',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderAdmin],
  templateUrl: './ver-socio.html',
  styleUrl: './ver-socio.css',
})
export class VerSocio implements OnInit {
  // Inyecciones modernas
  private socioServicio = inject(SocioServicio);
  private rubroServicio = inject(RubroServicio);
  private rubroSocioServicio = inject(RubroSocioServicio);
  private route = inject(ActivatedRoute);

  // Estados reactivos con Signals
  socio = signal<any>(null);
  vehiculos = signal<any[]>([]);
  rubrosDisponibles = signal<any[]>([]);
  rubrosAsociados = signal<any[]>([]);
  cargando = signal<boolean>(true);
  procesando = signal<boolean>(false);
  idProc = signal<number>(0);
  id = signal<number>(0);
  rubroSeleccionado = signal<number | null>(null);
  mostrarSelectorRubros = signal<boolean>(false);

  ngOnInit(): void {
    // Obtenemos el ID de la URL
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id.set(Number(idParam));
      this.obtenerDatosSocio();
    }
  }

  obtenerDatosSocio() {
    this.cargando.set(true);
    this.socioServicio.verSocio(this.id()).subscribe({
      next: (data) => {
        this.socio.set(data);
        this.obtenerVehiculos();
        this.obtenerRubrosDisponibles();
        this.obtenerRubrosAsociados();
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar socio:', error);
        this.cargando.set(false);
      }
    });
  }

  obtenerVehiculos() {
    this.socioServicio.obtenerVehiculosSocio(this.id()).subscribe({
      next: (data) => {
        this.vehiculos.set(data);
      },
      error: (error) => {
        console.error('Error al cargar vehículos:', error);
        this.vehiculos.set([]);
      }
    });
  }

  obtenerRubrosDisponibles() {
    this.rubroServicio.obtenerActivos().subscribe({
      next: (data) => {
        this.rubrosDisponibles.set(data);
      },
      error: (error) => {
        console.error('Error al cargar rubros:', error);
        this.rubrosDisponibles.set([]);
      }
    });
  }

  obtenerRubrosAsociados() {
    this.rubroSocioServicio.obtenerPorSocio(this.id()).subscribe({
      next: (data) => {
        this.rubrosAsociados.set(data);
      },
      error: (error) => {
        console.error('Error al cargar rubros asociados:', error);
        this.rubrosAsociados.set([]);
      }
    });
  }

  asociarRubro() {
    if (!this.rubroSeleccionado()) {
      alert('Selecciona un rubro para asociar');
      return;
    }

    this.procesando.set(true);
    const rubroSocioDTO = {
      idSocio: this.id(),
      idRubro: this.rubroSeleccionado()
    };

    this.rubroSocioServicio.crear(rubroSocioDTO as any).subscribe({
      next: () => {
        alert('Rubro asociado correctamente');
        this.rubroSeleccionado.set(null);
        this.mostrarSelectorRubros.set(false);
        this.obtenerRubrosAsociados();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al asociar rubro:', error);
        alert(error.error?.message || 'Error al asociar el rubro');
        this.procesando.set(false);
      }
    });
  }

  desasociarRubro(rubroId: number) {
    if (!confirm('¿Deseas desasociar este rubro del socio?')) {
      return;
    }

    this.procesando.set(true);
    this.rubroSocioServicio.eliminarRelacion(rubroId, this.id()).subscribe({
      next: () => {
        alert('Rubro desasociado correctamente');
        this.obtenerRubrosAsociados();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al desasociar rubro:', error);
        alert('Error al desasociar el rubro');
        this.procesando.set(false);
      }
    });
  }

  suspenderSocio() {
    if (!confirm('¿Estás seguro de que deseas suspender este socio?')) {
      return;
    }
    this.procesando.set(true);
    this.socioServicio.suspenderSocio(this.id()).subscribe({
      next: () => {
        alert('Socio suspendido correctamente');
        this.obtenerDatosSocio();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al suspender socio:', error);
        alert('Error al suspender socio');
        this.procesando.set(false);
      }
    });
  }

  bloquearSocio() {
    if (!confirm('¿Estás seguro de que deseas bloquear este socio?')) {
      return;
    }
    this.procesando.set(true);
    this.socioServicio.bloquearSocio(this.id()).subscribe({
      next: () => {
        alert('Socio bloqueado correctamente');
        this.obtenerDatosSocio();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al bloquear socio:', error);
        alert('Error al bloquear socio');
        this.procesando.set(false);
      }
    });
  }

  activarSocio() {
    if (!confirm('¿Estás seguro de que deseas activar este socio?')) {
      return;
    }
    this.procesando.set(true);
    this.socioServicio.activarSocio(this.id()).subscribe({
      next: () => {
        alert('Socio activado correctamente');
        this.obtenerDatosSocio();
        this.procesando.set(false);
      },
      error: (error) => {
        console.error('Error al activar socio:', error);
        alert('Error al activar socio');
        this.procesando.set(false);
      }
    });
  }

  // Verifica si un rubro está asociado al socio
  isRubroAsociado(idRubro: number): boolean {
    return this.rubrosAsociados().some(r => r.idRubro === idRubro);
  }
}