import { Component } from '@angular/core';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-header-admin',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './header-admin.html',
  styleUrl: './header-admin.css',
})
export class HeaderAdmin {

  constructor(private authServicio: AuthServicio) {}
  
    logout() {
      this.authServicio.logout();
    }

}
