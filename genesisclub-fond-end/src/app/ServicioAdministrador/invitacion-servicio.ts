import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InvitacionServicio {

  constructor(private http: HttpClient) {}

  private urlInvitaciones = `${environment.apiUrl}/invitacion/crear`;

  crearInvitacion(id: number, email: string): Observable<any> {
    const url = `${this.urlInvitaciones}/${id}`;
    
    // IMPORTANTE: Enviamos 'emailDestino' para que coincida con el DTO de Java
    const body = { 
      emailDestino: email 
    };

    return this.http.post<any>(url, body);
  }
  
}
