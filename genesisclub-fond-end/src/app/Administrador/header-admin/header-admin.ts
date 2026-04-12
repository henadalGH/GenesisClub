import { Component } from '@angular/core';
import { Router, RouterLink } from "@angular/router";

@Component({
  selector: 'app-header-admin',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './header-admin.html',
  styleUrl: './header-admin.css',
})
export class HeaderAdmin {

   menuAbierto = false;

  constructor(private router: Router) {}

  toggleMenu() {
    this.menuAbierto = !this.menuAbierto;
  }

  esActivoGrupo(rutas: string[]): boolean {
  return rutas.some(ruta => this.router.url.includes(ruta));
 }

  cerrarMenu() {
    this.menuAbierto = false;
  }

  logout() {
    // lógica real después (token, etc)
    console.log('Cerrar sesión');
    this.router.navigate(['/login']);
  }

  esActivo(ruta: string): boolean {
    return this.router.url.includes(ruta);
  }
}
