import { Component } from '@angular/core';
import { Router, RouterLink } from "@angular/router";

@Component({
  selector: 'app-inicio',
  imports: [RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio {
 
   modalAbierto = false;

  constructor(private router: Router) {}

  abrirModal() {
    this.modalAbierto = true;
  }

  cerrarModal() {
    this.modalAbierto = false;
  }

  irRegistro(tipo: string) {
    this.modalAbierto = false;

    if (tipo === 'jugador') {
      this.router.navigate(['/solicitud-jugador']);
    } else {
      this.router.navigate(['/solicitud']);
    }
  }
}

