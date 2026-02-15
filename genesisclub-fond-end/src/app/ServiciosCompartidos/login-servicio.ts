import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class LoginServicio {

  private urlLogin = `${environment.apiUrl}/auth/login`;

  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<any>(this.urlLogin, { email, password });
  }
}
