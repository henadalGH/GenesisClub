import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PendientesServicios {

  private urlPendientes = `${environment.apiUrl}/solicitud/pendientes`;
  private urlActualizar = `${environment.apiUrl}/solicitud/actualizar`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  listarPendientes() {
    return this.http.get<any[]>(this.urlPendientes, {
      headers: this.getHeaders()
    });
  }

  modificaEstado(id: number, estado: 'ACEPTADA' | 'RECHAZADA') {
    const url = `${this.urlActualizar}/${id}`;
    const params = new HttpParams().set('nuevoEstado', estado);

    // Eliminamos 'headers: this.getHeaders()'
    // El interceptor se encargará de inyectar el token automáticamente
    return this.http.put<any>(url, {}, { params });
}
}
