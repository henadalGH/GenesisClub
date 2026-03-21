import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HeaderAdmin } from "../header-admin/header-admin";
import { JugadorServicio } from '../../ServicioAdministrador/jugador-servicio';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-lista-jugador',
  standalone: true,
  imports: [HeaderAdmin, CommonModule],
  templateUrl: './lista-jugador.html',
  styleUrl: './lista-jugador.css',
})
export class ListaJugador implements OnInit {

  jugadores: any[] = [];

  constructor(
    private jugadorServicio: JugadorServicio,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.obtenerJugadores();
  }

  obtenerJugadores() {
    this.jugadorServicio.obtenerJugadores().subscribe({
      next: (respuesta: any) => {
        this.jugadores = [...respuesta];
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener jugadores:', err);
      }
    });
  }

  verJugador(id: number) {
    this.router.navigate(['/verJugador', id])
  }
}
