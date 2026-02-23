import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SocioServicio {

  private urlObtener = `${environment.apiUrl}/socio/todos`;
  private utlVerSocio = `${environment.apiUrl}/socio`;

  constructor(private http: HttpClient) {}

  obtenerSocio() {


    return this.http.get(this.urlObtener);
  }

  verSocio(id: number) {
  const url = `${this.utlVerSocio}/${id}`;

  return this.http.get<any>(url);
}
}
