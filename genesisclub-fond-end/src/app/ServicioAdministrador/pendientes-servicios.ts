import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PendientesServicios {

  constructor(private http: HttpClient){}

  private urlPendientes = `${environment.apiUrl}/solicitud/pendientes`;
  

  listarPendientes() {

    const token = localStorage.getItem('token'); // mismo token que guardaste al loguearte

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.get<any>(this.urlPendientes, { headers });
  }
}
