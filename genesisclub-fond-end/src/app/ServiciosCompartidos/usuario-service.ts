import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private baseUrl = `${environment.apiUrl}/usuario`;

  constructor(private http: HttpClient) {}

  // 🔎 Buscar por provincia
  buscarPorProvincia(provincia: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/provincia/${provincia}`);
  }

  // 🌎 Buscar por zona
  buscarPorZona(zona: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/zona/${zona}`);
  }
}