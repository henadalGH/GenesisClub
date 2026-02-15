import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginServicio } from './login-servicio';

@Injectable({
  providedIn: 'root',
})
export class AuthServicio {

  private loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(this.hasToken());

  constructor(
    private router: Router,
    private loginServicio: LoginServicio
  ) {}

  // ========================
  // LOGIN
  // ========================
  login(email: string, password: string): Observable<any> {
    return this.loginServicio.login(email, password).pipe(
      tap((resp: any) => {
        if (resp && resp.token) {
          localStorage.setItem('token', resp.token);
          localStorage.setItem('rol', resp.rol);
          localStorage.setItem('email', email);
          this.loggedIn.next(true);
        } else {
          throw new Error('Usuario o contraseña incorrectos');
        }
      })
    );
  }

  // ========================
  // LOGOUT
  // ========================
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('email');
    this.loggedIn.next(false);
    this.router.navigate(['/login']);
  }

  // ========================
  // HELPERS
  // ========================
  isLogged(): boolean {
    return this.hasToken();
  }

  getLoggedInObservable(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  getRol(): string | null {
    return localStorage.getItem('rol');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  redirectByRole(rol: string): void {
    switch (rol) {
      case 'ADMIN':
        this.router.navigate(['/inicioAdmin']);
        break;
      case 'SOCIO':
        this.router.navigate(['/inicioSocio']);
        break;

      case 'JUGADOR':
        this.router.navigate(['/inicioJugador']);
      break;
      default:
        this.router.navigate(['/inicio']);
    }
  }
}
