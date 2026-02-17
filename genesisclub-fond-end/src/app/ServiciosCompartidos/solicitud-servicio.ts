import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SolicitudServicio {

  private urlSolicitud = `${environment.apiUrl}/solicitud/nuevo`;

  constructor(private http: HttpClient) {}

  enviarSolicitud(
    nombre: string,
    apellido: string,
    email: string,
    contacto: string,
    password: string  // agregado
  ) {
    return this.http.post<any>(this.urlSolicitud, {
      nombre,
      apellido,
      email,
      contacto,
      password // enviado al backend
    });
  }
}
