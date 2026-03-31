import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { RubrosJugador } from '../Rubro/rubros-jugador';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';

@Component({
  selector: 'app-incio-jugador',
  standalone: true,
  imports: [RouterModule, RubrosJugador],
  templateUrl: './incio-jugador.html',
  styleUrl: './incio-jugador.css',
})
export class IncioJugador {
  constructor(private authServicio: AuthServicio) {}

  logout() {
    this.authServicio.logout();
  }
}
