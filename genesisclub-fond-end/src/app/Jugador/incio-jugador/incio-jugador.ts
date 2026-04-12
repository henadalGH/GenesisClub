import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { HeaderJugador } from "../header-jugador/header-jugador";

@Component({
  selector: 'app-incio-jugador',
  standalone: true,
  imports: [RouterModule, HeaderJugador],
  templateUrl: './incio-jugador.html',
  styleUrl: './incio-jugador.css',
})
export class IncioJugador {
  constructor(private authServicio: AuthServicio) {}

  logout() {
    this.authServicio.logout();
  }
}
