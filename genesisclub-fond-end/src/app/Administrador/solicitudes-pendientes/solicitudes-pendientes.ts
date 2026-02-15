import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HeaderAdmin } from "../header-admin/header-admin";
import { PendientesServicios } from '../../ServicioAdministrador/pendientes-servicios';
@Component({
  selector: 'app-solicitudes-pendientes',
  templateUrl: './solicitudes-pendientes.html',
  styleUrls: ['./solicitudes-pendientes.css'],
  imports: [HeaderAdmin],
})
export class SolicitudesPendientes implements OnInit {

  pendiente: any[] = [];

  constructor(private pendientesServicios: PendientesServicios,
              private cd: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.verPendientes();
  }

  verPendientes() {
    this.pendientesServicios.listarPendientes().subscribe(
      (respuesta: any) => {
        this.pendiente = respuesta;
        this.cd.detectChanges();
      }
    );
  }
}