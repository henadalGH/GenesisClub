import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { UsuarioService } from '../../../ServiciosCompartidos/usuario-service';

@Component({
  selector: 'app-mapa',
  standalone: true,
  templateUrl: './mapa.html',
  styleUrl: './mapa.css',
})
export class MapaComponent implements OnInit {

  private map: any;
  private markers: L.Marker[] = []; // 🔥 para limpiar bien

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.initMap();

    // 🔥 esperar render real
    setTimeout(() => {
      this.map.invalidateSize();
      this.cargarUsuarios('LITORAL');
    }, 300);
  }

  private initMap(): void {
    this.map = L.map('map').setView([-32.95, -60.66], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);
  }

  cargarUsuarios(zona: string): void {

    this.usuarioService.buscarPorZona(zona)
      .subscribe(data => {

        // 🔥 limpiar marcadores anteriores
        this.markers.forEach(m => this.map.removeLayer(m));
        this.markers = [];

        data.forEach(user => {

          if (user.ubicacion?.latitud != null && user.ubicacion?.longitud != null) {

            const marker = L.marker([
              user.ubicacion.latitud,
              user.ubicacion.longitud
            ]).addTo(this.map);

            marker.bindPopup(`
              <b>${user.nombre}</b><br>
              ${user.ubicacion.ciudad}, ${user.ubicacion.provincia}
            `);

            this.markers.push(marker); // 🔥 guardar referencia
          }
        });

        // 🔥 ajustar zoom automáticamente
        if (this.markers.length > 0) {
          const group = L.featureGroup(this.markers);
          this.map.fitBounds(group.getBounds());
        }

      });
  }

  cambiarZona(event: any) {
    const zona = event.target.value;
    this.cargarUsuarios(zona);
  }
}