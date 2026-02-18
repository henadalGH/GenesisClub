import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SocioServicio {

  private urlObtener = `${environment.apiUrl}/socio/todos`;

  constructor(private http: HttpClient) {}

  obtenerSocio() {

    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.get(this.urlObtener, { headers });
  }
}
