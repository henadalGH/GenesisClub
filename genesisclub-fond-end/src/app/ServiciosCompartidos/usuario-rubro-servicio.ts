import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioRubroDTO } from '../Modelos/rubro.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioRubroServicio {

  private baseUrl = 'http://localhost:8080/api/usuario-rubro';

  constructor(private http: HttpClient) {}

  // ===============================
  // OBTENER TODOS
  // ===============================
  obtenerTodos(): Observable<UsuarioRubroDTO[]> {
    return this.http.get<UsuarioRubroDTO[]>(this.baseUrl);
  }

  // ===============================
  // OBTENER POR ID
  // ===============================
  obtenerPorId(id: number): Observable<UsuarioRubroDTO> {
    return this.http.get<UsuarioRubroDTO>(`${this.baseUrl}/${id}`);
  }

  // ===============================
  // CREAR
  // ===============================
  crear(usuarioRubro: UsuarioRubroDTO): Observable<UsuarioRubroDTO> {
    return this.http.post<UsuarioRubroDTO>(this.baseUrl, usuarioRubro);
  }

  // ===============================
  // ACTUALIZAR
  // ===============================
  actualizar(id: number, usuarioRubro: UsuarioRubroDTO): Observable<UsuarioRubroDTO> {
    return this.http.put<UsuarioRubroDTO>(`${this.baseUrl}/${id}`, usuarioRubro);
  }

  // ===============================
  // ELIMINAR
  // ===============================
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

}
