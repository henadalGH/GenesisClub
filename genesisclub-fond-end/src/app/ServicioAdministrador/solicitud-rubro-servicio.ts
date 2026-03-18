import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface SolicitudRubroDTO {
  id: number;
  socio: any; // Ajustar según necesidad
  rubro: any;
  estado: string;
  fechaCreacion: string;
}

export interface CrearSolicitudRubroDTO {
  socioId: number;
  rubroId: number;
  claveAcceso: string;
}

@Injectable({
  providedIn: 'root',
})
export class SolicitudRubroServicio {

  private baseUrl = `${environment.apiUrl}/solicitudes-rubro`;

  constructor(private http: HttpClient) {}

  crearSolicitud(dto: CrearSolicitudRubroDTO): Observable<SolicitudRubroDTO> {
    return this.http.post<SolicitudRubroDTO>(this.baseUrl, dto);
  }

  // Helper para extraer mensaje de error desde backend
  private getErrorMessage(error: any): string {
    if (error?.error?.error) {
      return error.error.error;
    }
    return error?.message || 'Error desconocido';
  }

  aprobarSolicitud(id: number): Observable<SolicitudRubroDTO> {
    return this.http.put<SolicitudRubroDTO>(`${this.baseUrl}/${id}/aprobar`, {});
  }

  rechazarSolicitud(id: number): Observable<SolicitudRubroDTO> {
    return this.http.put<SolicitudRubroDTO>(`${this.baseUrl}/${id}/rechazar`, {});
  }

  // Método adicional para obtener solicitudes pendientes (si se agrega endpoint)
  obtenerSolicitudesPendientes(): Observable<SolicitudRubroDTO[]> {
    return this.http.get<SolicitudRubroDTO[]>(`${this.baseUrl}/pendientes`);
  }

}