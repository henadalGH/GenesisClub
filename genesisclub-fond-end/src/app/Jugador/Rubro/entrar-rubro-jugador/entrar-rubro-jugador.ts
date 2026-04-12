import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RubroServicio } from '../../../ServiciosCompartidos/rubro-servicio';

@Component({
  selector: 'app-entrar-rubro-jugador',
  imports: [],
  templateUrl: './entrar-rubro-jugador.html',
  styleUrl: './entrar-rubro-jugador.css',
})
export class EntrarRubroJugador implements OnInit {
  private rubroService = inject(RubroServicio);
  private route = inject(ActivatedRoute);

  rubro = signal<any>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      return;
    }

    this.rubroService.obtenerPorId(id).subscribe({
      next: (data) => this.rubro.set(data),
      error: (err) => console.error('No se pudo cargar rubro', err)
    });
  }
}

