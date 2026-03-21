import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { ResponceDTO, CrearSolicitudSocioDTO, CrearSolicitudJugadorDTO, RegistroInvitadoDTO } from '../Modelos/usuario.model';

@Injectable({
  providedIn: 'root',
})
export class SolicitudServicio {
  private urlSolicitud = `${environment.apiUrl}/solicitud/socio/nuevo`;
  private urlSolicitudJugador = `${environment.apiUrl}/solicitud/jugador`;
  private urlInvitado = `${environment.apiUrl}/solicitud/socio/registro-invitado`;

  constructor(private http: HttpClient) {}

  // Recibe un objeto tipado con datos del socio
  enviarSolicitud(datos: CrearSolicitudSocioDTO): Observable<ResponceDTO> {
    return this.http.post<ResponceDTO>(this.urlSolicitud, datos);
  }

  enviarSolicitudJugador(datos: CrearSolicitudJugadorDTO): Observable<ResponceDTO> {
    return this.http.post<ResponceDTO>(this.urlSolicitudJugador, datos);
  }

  registrarInvitado(datos: RegistroInvitadoDTO, token: string): Observable<ResponceDTO> {
    // Agregamos el token como parámetro (?token=xxx)
    const params = new HttpParams().set('token', token);
    
    return this.http.post<ResponceDTO>(this.urlInvitado, datos, { params });
  }
}