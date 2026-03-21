import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NotificacionesComponent } from './ComponentesCompartidos/notificaciones/notificaciones';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NotificacionesComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'genesisclub-fond-end';
}
