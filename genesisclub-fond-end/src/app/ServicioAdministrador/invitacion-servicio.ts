import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class InvitacionServicio {

  constructor(
    private http: HttpClient
  ){}

  private urlInvitaciones = `${environment.apiUrl}/api/invitacion/crear`

  crearInvitacion(id: number, email: string){

    const url = `${this.urlInvitaciones}/${id}`;
    return this.http.post<any>(url, {email});
  }
  
}
