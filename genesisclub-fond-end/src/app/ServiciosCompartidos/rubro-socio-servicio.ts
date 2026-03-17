import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RubroSocioDTO } from '../Modelos/rubro.model';

@Injectable({
  providedIn: 'root'
})
export class RubroSocioServicio {

  private baseUrl = 'http://localhost:8080/api/rubro-socio';

  constructor(private http: HttpClient) {}

  // ===============================
  // OBTENER TODOS
  // ===============================
  obtenerTodos(): Observable<RubroSocioDTO[]> {
    return this.http.get<RubroSocioDTO[]>(this.baseUrl);
  }

  // ===============================
  // OBTENER POR ID
  // ===============================
  obtenerPorId(id: number): Observable<RubroSocioDTO> {
    return this.http.get<RubroSocioDTO>(`${this.baseUrl}/${id}`);
  }

  // ===============================
  // OBTENER POR RUBRO
  // ===============================
  obtenerPorRubro(idRubro: number): Observable<RubroSocioDTO[]> {
    return this.http.get<RubroSocioDTO[]>(`${this.baseUrl}/rubro/${idRubro}`);
  }

  // ===============================
  // OBTENER POR SOCIO
  // ===============================
  obtenerPorSocio(idSocio: number): Observable<RubroSocioDTO[]> {
    return this.http.get<RubroSocioDTO[]>(`${this.baseUrl}/socio/${idSocio}`);
  }

  // ===============================
  // CREAR
  // ===============================
  crear(rubroSocio: RubroSocioDTO): Observable<RubroSocioDTO> {
    return this.http.post<RubroSocioDTO>(this.baseUrl, rubroSocio);
  }

  // ===============================
  // ACTUALIZAR
  // ===============================
  actualizar(id: number, rubroSocio: RubroSocioDTO): Observable<RubroSocioDTO> {
    return this.http.put<RubroSocioDTO>(`${this.baseUrl}/${id}`, rubroSocio);
  }

  // ===============================
  // ELIMINAR
  // ===============================
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // ===============================
  // VERIFICAR RELACIÓN
  // ===============================
  existeRelacion(idRubro: number, idSocio: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/existe/rubro/${idRubro}/socio/${idSocio}`);
  }

  // ===============================
  // ELIMINAR RELACIÓN
  // ===============================
  eliminarRelacion(idRubro: number, idSocio: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/rubro/${idRubro}/socio/${idSocio}`);
  }

  // ===============================
  // ASOCIAR POR CLAVE DE ACCESO
  // ===============================
  asociarPorClaveAcceso(idSocio: number, claveAcceso: string): Observable<RubroSocioDTO> {
    return this.http.post<RubroSocioDTO>(`${this.baseUrl}/socio/${idSocio}/clave/${claveAcceso}`, {});
  }

}
