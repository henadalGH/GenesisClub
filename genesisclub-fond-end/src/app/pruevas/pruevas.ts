import { Component } from '@angular/core';
import { MapaComponent } from "../ComponentesCompartidos/Mapa/mapa/mapa";

@Component({
  selector: 'app-pruevas',
  standalone: true, // ✅ FALTA ESTO
  imports: [MapaComponent],
  templateUrl: './pruevas.html',
  styleUrl: './pruevas.css',
})
export class Pruevas {

}