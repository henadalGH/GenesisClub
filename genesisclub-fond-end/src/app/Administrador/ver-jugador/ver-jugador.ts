import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { JugadorServicio } from '../../ServicioAdministrador/jugador-servicio';
import { HeaderAdmin } from '../header-admin/header-admin';

@Component({
  selector: 'app-ver-jugador',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderAdmin],
  templateUrl: './ver-jugador.html',
  styleUrls: ['./ver-jugador.css'],
})
export class VerJugador implements OnInit {
  private jugadorServicio = inject(JugadorServicio);
  private route = inject(ActivatedRoute);

  jugador = signal<any>(null);
  cargando = signal<boolean>(true);
  id = signal<number>(0);

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id.set(Number(idParam));
      this.obtenerDatosJugador();
    }
  }

  obtenerDatosJugador() {
    this.cargando.set(true);
    this.jugadorServicio.obtenerJugadorPorId(this.id()).subscribe({
      next: (data) => {
        this.jugador.set(data);
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar jugador:', error);
        this.cargando.set(false);
      }
    });
  }
}
