import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SocioServicio } from '../../ServicioAdministrador/socio-servicio';
import { HeaderAdmin } from '../header-admin/header-admin';

@Component({
  selector: 'app-ver-socio',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderAdmin],
  templateUrl: './ver-socio.html',
  styleUrls: ['./ver-socio.css'], // <-- Corregido
})
export class VerSocio implements OnInit {
  // Inyecciones modernas
  private socioServicio = inject(SocioServicio);
  private route = inject(ActivatedRoute);

  // Estados reactivos con Signals
  socio = signal<any>(null);
  cargando = signal<boolean>(true);
  id = signal<number>(0);

  ngOnInit(): void {
    // Obtenemos el ID de la URL
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.id.set(Number(idParam));
      this.obtenerDatosSocio();
    }
  }

  obtenerDatosSocio() {
    this.cargando.set(true); // Por si quieres reactivar carga
    this.socioServicio.verSocio(this.id()).subscribe({
      next: (data) => {
        this.socio.set(data);
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar socio:', error);
        this.cargando.set(false);
      }
    });
  }
}