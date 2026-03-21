import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, throwError, timeout } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface SolicitudRubroDTO {
  id: number;
  socio: SocioInfoDTO;
  rubro: RubroInfoDTO;
  estado: string;
  fechaCreacion: string;
}

export interface SocioInfoDTO {
  id: number;
  nombreUsuario: string;
  emailUsuario: string;
}

export interface RubroInfoDTO {
  id: number;
  nombre: string;
  descripcion: string;
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
    return this.http.post<SolicitudRubroDTO>(this.baseUrl, dto).pipe(
      catchError(error => {
        console.error('Error al crear solicitud:', error);
        return throwError(() => new Error(this.getErrorMessage(error)));
      })
    );
  }

  // Helper para extraer mensaje de error desde backend
  private getErrorMessage(error: any): string {
    if (error?.error?.error) {
      return error.error.error;
    }
    if (error?.error?.message) {
      return error.error.message;
    }
    return error?.message || 'Error desconocido';
  }

  aprobarSolicitud(id: number): Observable<SolicitudRubroDTO> {
    const url = `${this.baseUrl}/${id}/aprobar`;
    console.log(`📤 PUT ${url}`);
    
    return this.http.put<SolicitudRubroDTO>(url, {}).pipe(
      timeout(10000),
      tap(data => {
        console.log('✅ Solicitud aprobada:', data);
      }),
      catchError(error => {
        console.error('❌ Error al aprobar solicitud:', error);
        return throwError(() => new Error(this.getErrorMessage(error)));
      })
    );
  }

  rechazarSolicitud(id: number): Observable<SolicitudRubroDTO> {
    const url = `${this.baseUrl}/${id}/rechazar`;
    console.log(`📤 PUT ${url}`);
    
    return this.http.put<SolicitudRubroDTO>(url, {}).pipe(
      timeout(10000),
      tap(data => {
        console.log('✅ Solicitud rechazada:', data);
      }),
      catchError(error => {
        console.error('❌ Error al rechazar solicitud:', error);
        return throwError(() => new Error(this.getErrorMessage(error)));
      })
    );
  }

  obtenerSolicitudesPendientes(): Observable<SolicitudRubroDTO[]> {
    const url = `${this.baseUrl}/pendientes`;
    console.log(`📤 GET ${url}`);
    
    return this.http.get<SolicitudRubroDTO[]>(url).pipe(
      timeout(10000), // 10 segundos timeout
      tap(data => {
        console.log('✅ Respuesta recibida:', data);
      }),
      catchError(error => {
        console.error('❌ Error al obtener solicitudes pendientes:', error);
        const mensaje = this.getErrorMessage(error);
        console.error('   Mensaje:', mensaje);
        return throwError(() => new Error(mensaje));
      })
    );
  }
}