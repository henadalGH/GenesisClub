import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { HeaderAdmin } from "../header-admin/header-admin"; // ajusta la ruta según tu proyecto

@Component({
  selector: 'app-inicio-admin',
  standalone: true,
  imports: [HeaderAdmin, RouterLink],
  templateUrl: './inicio-admin.html',
  styleUrl: './inicio-admin.css', // CORREGIDO
})
export class InicioAdmin {

  constructor(private authServicio: AuthServicio) {}

  logout() {
    this.authServicio.logout();
  }
}
