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

  listarPendientes() {
    return this.http.get<any[]>(this.urlPendientes);
  }

  modificaEstado(id: number, estado: 'ACEPTADA' | 'RECHAZADA') {
    const url = `${this.urlActualizar}/${id}`;
    const params = new HttpParams().set('nuevoEstado', estado);

    return this.http.put<any>(url, {}, { params });
}
}
