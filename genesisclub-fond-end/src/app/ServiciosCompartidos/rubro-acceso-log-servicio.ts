import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RubroAccesoLogDTO } from '../Modelos/rubro.model';

@Injectable({
  providedIn: 'root'
})
export class RubroAccesoLogServicio {

  private baseUrl = 'http://localhost:8080/api/rubro-acceso-log';

  constructor(private http: HttpClient) {}

  // ===============================
  // OBTENER TODOS
  // ===============================
  obtenerTodos(): Observable<RubroAccesoLogDTO[]> {
    return this.http.get<RubroAccesoLogDTO[]>(this.baseUrl);
  }

  // ===============================
  // OBTENER POR ID
  // ===============================
  obtenerPorId(id: number): Observable<RubroAccesoLogDTO> {
    return this.http.get<RubroAccesoLogDTO>(`${this.baseUrl}/${id}`);
  }

  // ===============================
  // OBTENER POR RUBRO
  // ===============================
  obtenerPorRubro(idRubro: number): Observable<RubroAccesoLogDTO[]> {
    return this.http.get<RubroAccesoLogDTO[]>(`${this.baseUrl}/rubro/${idRubro}`);
  }

  // ===============================
  // OBTENER POR USUARIO
  // ===============================
  obtenerPorUsuario(idUsuario: number): Observable<RubroAccesoLogDTO[]> {
    return this.http.get<RubroAccesoLogDTO[]>(`${this.baseUrl}/usuario/${idUsuario}`);
  }

  // ===============================
  // OBTENER POR ÉXITO
  // ===============================
  obtenerPorExito(exitoso: boolean): Observable<RubroAccesoLogDTO[]> {
    return this.http.get<RubroAccesoLogDTO[]>(`${this.baseUrl}/exitoso/${exitoso}`);
  }

  // ===============================
  // OBTENER EXITOSOS POR RUBRO
  // ===============================
  obtenerExitosasPorRubro(idRubro: number): Observable<RubroAccesoLogDTO[]> {
    return this.http.get<RubroAccesoLogDTO[]>(`${this.baseUrl}/rubro/${idRubro}/exitosos`);
  }

  // ===============================
  // OBTENER POR FECHA
  // ===============================
  obtenerPorFecha(fechaInicio: string, fechaFin: string): Observable<RubroAccesoLogDTO[]> {
    return this.http.get<RubroAccesoLogDTO[]>(`${this.baseUrl}/por-fecha?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }

  // ===============================
  // CREAR
  // ===============================
  crear(log: RubroAccesoLogDTO): Observable<RubroAccesoLogDTO> {
    return this.http.post<RubroAccesoLogDTO>(this.baseUrl, log);
  }

  // ===============================
  // ELIMINAR
  // ===============================
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

}
