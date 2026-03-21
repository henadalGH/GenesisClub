import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, timeout } from 'rxjs';
import { SolicitudSocioDTO } from '../Modelos/usuario.model';
import { catchError, tap } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PendientesServicios {

  private urlPendientes = `${environment.apiUrl}/solicitud/socio/pendientes`;
  private urlActualizar = `${environment.apiUrl}/solicitud/socio/actualizar`;

  constructor(private http: HttpClient) {}

  listarPendientes(): Observable<SolicitudSocioDTO[]> {
    console.log(`📤 GET ${this.urlPendientes}`);
    
    return this.http.get<SolicitudSocioDTO[]>(this.urlPendientes).pipe(
      timeout(10000),
      tap(data => {
        console.log('✅ Socios pendientes recibidos:', data);
      }),
      catchError(error => {
        console.error('❌ Error al listar pendientes de socios:', error);
        return throwError(() => error);
      })
    );
  }

  modificaEstado(id: number, estado: 'ACEPTADA' | 'RECHAZADA'): Observable<any> {
    const url = `${this.urlActualizar}/${id}`;
    const params = new HttpParams().set('nuevoEstado', estado);
    
    console.log(`📤 PUT ${url}?nuevoEstado=${estado}`);

    return this.http.put<any>(url, {}, { params }).pipe(
      timeout(10000),
      tap(data => {
        console.log('✅ Estado actualizado:', data);
      }),
      catchError(error => {
        console.error('❌ Error al modificar estado:', error);
        return throwError(() => error);
      })
    );
  }
}
