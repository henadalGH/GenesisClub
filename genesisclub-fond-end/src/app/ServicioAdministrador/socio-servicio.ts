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