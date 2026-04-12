import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface AdminResumenDTO {
  sociosActivos: number;
  jugadoresActivos: number;
  solicitudesPendientes: number;
  solicitudesJugadoresPendientes: number;
  solicitudesRubrosPendientes: number;
  rubrosActivos: number;
}

@Injectable({
  providedIn: 'root',
})
export class AdminServicio {
  private baseUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  obtenerResumen(): Observable<AdminResumenDTO> {
    return this.http.get<AdminResumenDTO>(`${this.baseUrl}/resumen`);
  }
}
