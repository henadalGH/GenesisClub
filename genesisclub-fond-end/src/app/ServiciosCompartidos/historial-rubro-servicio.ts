import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { HistorialRubroDTO } from '../Modelos/rubro.model';

@Injectable({
  providedIn: 'root'
})
export class HistorialRubroServicio {

  private baseUrl = `${environment.apiUrl}/historial-rubro`;

  constructor(private http: HttpClient) {}

  // ===============================
  // OBTENER TODOS
  // ===============================
  obtenerTodos(): Observable<HistorialRubroDTO[]> {
    return this.http.get<HistorialRubroDTO[]>(this.baseUrl);
  }

  // ===============================
  // OBTENER POR ID
  // ===============================
  obtenerPorId(id: number): Observable<HistorialRubroDTO> {
    return this.http.get<HistorialRubroDTO>(`${this.baseUrl}/${id}`);
  }

  // ===============================
  // OBTENER POR RUBRO
  // ===============================
  obtenerPorRubro(idRubro: number): Observable<HistorialRubroDTO[]> {
    return this.http.get<HistorialRubroDTO[]>(`${this.baseUrl}/rubro/${idRubro}`);
  }

  // ===============================
  // OBTENER POR ADMIN
  // ===============================
  obtenerPorAdmin(idAdmin: number): Observable<HistorialRubroDTO[]> {
    return this.http.get<HistorialRubroDTO[]>(`${this.baseUrl}/admin/${idAdmin}`);
  }

  // ===============================
  // OBTENER POR FECHA
  // ===============================
  obtenerPorFecha(fechaInicio: string, fechaFin: string): Observable<HistorialRubroDTO[]> {
    return this.http.get<HistorialRubroDTO[]>(`${this.baseUrl}/por-fecha?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
  }

  // ===============================
  // CREAR
  // ===============================
  crear(historial: HistorialRubroDTO): Observable<HistorialRubroDTO> {
    return this.http.post<HistorialRubroDTO>(this.baseUrl, historial);
  }

  // ===============================
  // ELIMINAR
  // ===============================
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

}
