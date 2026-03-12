import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class JugadorServicio {
  private urlObtenerTodos = `${environment.apiUrl}/jugador/todos`;
  private urlObtenerPorId = `${environment.apiUrl}/jugador`;

  constructor(private http: HttpClient) {}

  /**
   * Obtiene la lista de todos los jugadores
   */
  obtenerJugadores(): Observable<any[]> {
    return this.http.get<any[]>(this.urlObtenerTodos);
  }

  /**
   * Obtiene los detalles de un jugador por ID
   */
  obtenerJugadorPorId(id: number): Observable<any> {
    const url = `${this.urlObtenerPorId}/${id}`;
    return this.http.get<any>(url);
  }
}
