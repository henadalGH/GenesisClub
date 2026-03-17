import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RubroDTO, RubroSocioDTO, HistorialRubroDTO, UsuarioRubroDTO, RubroAccesoLogDTO } from '../Modelos/rubro.model';

@Injectable({
  providedIn: 'root'
})
export class RubroServicio {

  private baseUrl = 'http://localhost:8080/api/rubro';

  constructor(private http: HttpClient) {}

  // ===============================
  // OBTENER TODOS LOS RUBROS
  // ===============================
  obtenerTodos(): Observable<RubroDTO[]> {
    return this.http.get<RubroDTO[]>(this.baseUrl);
  }

  // ===============================
  // OBTENER RUBROS ACTIVOS
  // ===============================
  obtenerActivos(): Observable<RubroDTO[]> {
    return this.http.get<RubroDTO[]>(`${this.baseUrl}/activos`);
  }

  // ===============================
  // OBTENER RUBRO POR ID
  // ===============================
  obtenerPorId(id: number): Observable<RubroDTO> {
    return this.http.get<RubroDTO>(`${this.baseUrl}/${id}`);
  }

  // ===============================
  // OBTENER RUBRO POR NOMBRE
  // ===============================
  obtenerPorNombre(nombre: string): Observable<RubroDTO> {
    return this.http.get<RubroDTO>(`${this.baseUrl}/nombre/${nombre}`);
  }

  // ===============================
  // OBTENER RUBRO POR CLAVE
  // ===============================
  obtenerPorClave(clave: string): Observable<RubroDTO> {
    return this.http.get<RubroDTO>(`${this.baseUrl}/clave/${clave}`);
  }

  // ===============================
  // BUSCAR RUBROS
  // ===============================
  buscar(nombre: string): Observable<RubroDTO[]> {
    return this.http.get<RubroDTO[]>(`${this.baseUrl}/buscar?nombre=${nombre}`);
  }

  // ===============================
  // CREAR RUBRO
  // ===============================
  crear(rubro: RubroDTO): Observable<RubroDTO> {
    return this.http.post<RubroDTO>(this.baseUrl, rubro);
  }

  // ===============================
  // ACTUALIZAR RUBRO
  // ===============================
  actualizar(id: number, rubro: RubroDTO): Observable<RubroDTO> {
    return this.http.put<RubroDTO>(`${this.baseUrl}/${id}`, rubro);
  }

  // ===============================
  // ELIMINAR RUBRO
  // ===============================
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // ===============================
  // ACTIVAR RUBRO
  // ===============================
  activar(id: number): Observable<RubroDTO> {
    return this.http.put<RubroDTO>(`${this.baseUrl}/${id}/activar`, {});
  }

  // ===============================
  // DESACTIVAR RUBRO
  // ===============================
  desactivar(id: number): Observable<RubroDTO> {
    return this.http.put<RubroDTO>(`${this.baseUrl}/${id}/desactivar`, {});
  }

}
