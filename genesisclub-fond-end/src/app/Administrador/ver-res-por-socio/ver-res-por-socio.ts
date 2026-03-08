import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RelacionSocioDTO, SocioServicio } from '../../ServicioAdministrador/socio-servicio';
import { HeaderAdmin } from "../header-admin/header-admin";

@Component({
  selector: 'app-ver-res-por-socio',
  standalone: true,
  imports: [CommonModule, HeaderAdmin],
  templateUrl: './ver-res-por-socio.html',
  styleUrl: './ver-res-por-socio.css',
})
export class VerResPorSocio implements OnInit {
  private route = inject(ActivatedRoute);
  private socioServicio = inject(SocioServicio);

  // Signals para manejar el estado
  public redSocio = signal<RelacionSocioDTO | null>(null);
  public cargando = signal<boolean>(true);

  ngOnInit(): void {
    // Capturamos el ID de la ruta corregida
    const id = Number(this.route.snapshot.paramMap.get('id'));
    
    if (id) {
      this.cargarRed(id);
    }
  }

  cargarRed(id: number) {
    this.socioServicio.verRedArbol(id).subscribe({
      next: (res) => {
        this.redSocio.set(res);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar la red', err);
        this.cargando.set(false);
      }
    });
  }
}
