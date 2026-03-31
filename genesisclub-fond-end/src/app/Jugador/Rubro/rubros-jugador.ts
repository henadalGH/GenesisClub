import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RubroServicio } from '../../ServiciosCompartidos/rubro-servicio';
import { RubroDTO } from '../../Modelos/rubro.model';

@Component({
  selector: 'app-rubros-jugador',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rubros-jugador.html',
  styleUrl: './rubros-jugador.css',
})
export class RubrosJugador implements OnInit {
  private rubroServicio = inject(RubroServicio);
  private router = inject(Router);

  rubrosDisponibles = signal<RubroDTO[]>([]);
  cargando = signal<boolean>(true);
  procesando = signal<boolean>(false);
  mensajeExito = signal<string>('');
  mensajeError = signal<string>('');
  claveInput = signal<{ [rubroId: number]: string }>({});

  ngOnInit(): void {
    this.cargarRubros();
  }

  cargarRubros(): void {
    this.cargando.set(true);
    this.rubroServicio.obtenerActivos().subscribe({
      next: (rubros: RubroDTO[]) => {
        this.rubrosDisponibles.set(rubros);
        this.cargando.set(false);
      },
      error: (error: any) => {
        console.error('Error al cargar rubros disponibles:', error);
        this.mensajeError.set('Error cargando rubros disponibles.');
        this.cargando.set(false);
      }
    });
  }

  obtenerClave(rubroId: number): string {
    return this.claveInput()[rubroId]?.trim() || '';
  }

  setClave(rubroId: number, valor: string): void {
    this.claveInput.set({ ...this.claveInput(), [rubroId]: valor });
  }

  entrarConClave(rubro: RubroDTO): void {
    if (!rubro || !rubro.id) {
      this.mensajeError.set('Rubro inválido.');
      return;
    }

    const clave = this.obtenerClave(rubro.id);
    if (!clave) {
      this.mensajeError.set('Ingresá la clave de acceso para continuar.');
      return;
    }

    if (clave !== rubro.claveAcceso) {
      this.mensajeError.set('Clave incorrecta. Verificá e intentá de nuevo.');
      return;
    }

    this.mensajeError.set('');
    this.mensajeExito.set(`✅ Acceso autorizado a ${rubro.nombre}. Redirigiendo...`);
    this.procesando.set(true);

    setTimeout(() => {
      this.procesando.set(false);
      this.mensajeExito.set('');
      this.claveInput.set({ ...this.claveInput(), [rubro.id]: '' });
      this.router.navigate(['entrarRubroJugador', rubro.id]);
    }, 300);
  }
}
