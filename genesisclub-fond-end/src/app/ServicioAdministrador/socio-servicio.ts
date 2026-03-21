import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

// Interfaz necesaria para el árbol de GenesisClub
export interface RelacionSocioDTO {
  idRelacion: number;
  idSocioHijo: number;
  nombreCompleto: string;
  nivel: number;
  fechaIngreso: string;
  descendientes: RelacionSocioDTO[];
}

@Injectable({
  providedIn: 'root',
})
export class SocioServicio {

  // Volvemos a tus rutas originales
  private urlObtener = `${environment.apiUrl}/socio/todos`;
  private utlVerSocio = `${environment.apiUrl}/socio`;
  // Esta es la ruta para la red según tu controlador de Spring Boot
  private urlRed = `${environment.apiUrl}/relacion-socio`;

  constructor(private http: HttpClient) {}

  obtenerSocio() {
    return this.http.get(this.urlObtener);
  }

  verSocio(id: number) {
    const url = `${this.utlVerSocio}/${id}`;
    return this.http.get<any>(url);
  }

  /**
   * Obtiene los vehículos de un socio
   */
  obtenerVehiculosSocio(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.utlVerSocio}/${id}/vehiculos`);
  }

  /**
   * Suspende a un socio
   */
  suspenderSocio(id: number): Observable<any> {
    return this.http.put<any>(`${this.utlVerSocio}/${id}/suspender`, {});
  }

  /**
   * Bloquea a un socio
   */
  bloquearSocio(id: number): Observable<any> {
    return this.http.put<any>(`${this.utlVerSocio}/${id}/bloquear`, {});
  }

  /**
   * Activa a un socio
   */
  activarSocio(id: number): Observable<any> {
    return this.http.put<any>(`${this.utlVerSocio}/${id}/activar`, {});
  }

  /**
   * Obtiene la estructura jerárquica (Árbol) del socio para el Admin
   */
  verRedArbol(id: number): Observable<RelacionSocioDTO> {
    return this.http.get<RelacionSocioDTO>(`${this.urlRed}/mi-red-arbol/${id}`);
  }

  /**
   * Obtiene la lista plana de la red
   */
  verRedLista(id: number): Observable<RelacionSocioDTO[]> {
    return this.http.get<RelacionSocioDTO[]>(`${this.urlRed}/mi-red-lista/${id}`);
  }
}