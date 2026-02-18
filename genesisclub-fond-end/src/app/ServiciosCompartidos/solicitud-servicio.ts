import { HttpClient } from '@angular/common/http';
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
  private urlSolicitud = `${environment.apiUrl}/solicitud/nuevo`;

  constructor(private http: HttpClient) {}

  // Recibe un solo objeto 'datos' (que contiene nombre, apellido, etc.)
  enviarSolicitud(datos: any): Observable<ResponceDTO> {
    return this.http.post<ResponceDTO>(this.urlSolicitud, datos);
  }
}