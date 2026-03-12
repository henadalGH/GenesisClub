import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

// Interfaz para saber qué nos responde el Java
export interface ResponceDTO {
  mensage: string;
  numOfErrors: number;
}

@Injectable({
  providedIn: 'root',
})
export class SolicitudServicio {
  private urlSolicitud = `${environment.apiUrl}/solicitud/socio/nuevo`;
  private urlSolicitudJugador = `${environment.apiUrl}/solicitud/jugador`;
  private urlInvitado = `${environment.apiUrl}/solicitud/socio/registro-invitado`;

  constructor(private http: HttpClient) {}

  // Recibe un solo objeto 'datos' (que contiene nombre, apellido, etc.)
  enviarSolicitud(datos: any): Observable<ResponceDTO> {
    return this.http.post<ResponceDTO>(this.urlSolicitud, datos);
  }

  enviarSolicitudJugador(datos: any): Observable<ResponceDTO> {
    return this.http.post<ResponceDTO>(this.urlSolicitudJugador, datos);
  }


  registrarInvitado(datos: any, token: string): Observable<ResponceDTO> {
    // Agregamos el token como parámetro (?token=xxx)
    const params = new HttpParams().set('token', token);
    
    return this.http.post<ResponceDTO>(this.urlInvitado, datos, { params });
  }
}