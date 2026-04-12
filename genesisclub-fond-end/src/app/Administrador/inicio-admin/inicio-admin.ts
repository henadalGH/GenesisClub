import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { HeaderAdmin } from "../header-admin/header-admin";
import { AdminServicio, AdminResumenDTO } from '../../ServicioAdministrador/admin-servicio';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-inicio-admin',
  standalone: true,
  imports: [CommonModule, HeaderAdmin, RouterLink],
  templateUrl: './inicio-admin.html',
  styleUrl: './inicio-admin.css',
})
export class InicioAdmin implements OnInit {

  cargandoResumen = false;
  resumen: AdminResumenDTO = {
    sociosActivos: 0,
    jugadoresActivos: 0,
    solicitudesPendientes: 0,
    solicitudesJugadoresPendientes: 0,
    solicitudesRubrosPendientes: 0,
    rubrosActivos: 0,
  };

  constructor(
    private authServicio: AuthServicio,
    private adminServicio: AdminServicio
  ) {}

  ngOnInit(): void {
    this.cargarResumen();
  }

  logout() {
    this.authServicio.logout();
  }

  private cargarResumen(): void {
    this.cargandoResumen = true;
    this.adminServicio.obtenerResumen().subscribe({
      next: (resumen) => {
        this.resumen = resumen;
        this.cargandoResumen = false;
      },
      error: () => {
        this.cargandoResumen = false;
      }
    });
  }
}
