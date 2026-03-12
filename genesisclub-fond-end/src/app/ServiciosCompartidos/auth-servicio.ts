import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap, catchError, throwError } from 'rxjs';
import { LoginServicio } from './login-servicio';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class AuthServicio {

  private loggedIn: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(this.hasToken());

  constructor(
    private router: Router,
    private loginServicio: LoginServicio
  ) {}

  // ========================
  // 🔐 LOGIN
  // ========================
  login(email: string, password: string): Observable<any> {
    return this.loginServicio.login(email, password).pipe(
      tap((resp: any) => {
        if (resp?.token) {
          localStorage.setItem('token', resp.token);
          localStorage.setItem('email', email);
          this.loggedIn.next(true);
        } else {
          throw new Error('Usuario o contraseña incorrectos');
        }
      }),
      catchError((err: any) => {
        // transform HTTP errors to friendly message
        let msg = 'Usuario o contraseña incorrectos';
        if (err.status === 0) {
          msg = 'No se pudo conectar al servidor';
        } else if (err.status === 401) {
          // prefer backend message if present
          msg = err.error?.message || 'Usuario o contraseña incorrectos';
        }
        return throwError(() => new Error(msg));
      })
    );
  }


  // ========================
  // 🚪 LOGOUT
  // ========================
  logout(): void {
    localStorage.clear();
    this.loggedIn.next(false);
    this.router.navigate(['/inicio']);
  }


  // ========================
  // ✅ HELPERS
  // ========================

  isLogged(): boolean {
    return this.hasToken();
  }

  getLoggedInObservable(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }


  // 🔥🔥🔥 MÉTODO CLAVE ARREGLADO
  // Lee rol del JWT + normaliza a ROLE_XXX
  getRol(): string | null {

    const token = this.getToken();
    if (!token) return null;

    const decoded: any = jwtDecode(token);


    let role =
      decoded.authorities?.[0] ||
      decoded.roles?.[0] ||
      decoded.role ||
      decoded.rol ||
      null;

    if (!role) return null;

    // ✅ Normalizamos formato
    // ADMIN -> ROLE_ADMIN
    if (!role.startsWith('ROLE_')) {
      role = `ROLE_${role}`;
    }

    return role;
  }


  // ========================
  // 🔥 ID desde JWT
  // ========================
  getUserId(): number | null {

    const token = this.getToken();
    if (!token) return null;

    const decoded: any = jwtDecode(token);

    return decoded.sub || decoded.id || null;
  }


  private hasToken(): boolean {
    return !!this.getToken();
  }


  // ========================
  // 🔁 REDIRECCIÓN POR ROL
  // ========================
  redirectByRole(): void {

    const rol = this.getRol();

    switch (rol) {

      case 'ROLE_ADMIN':
        this.router.navigate(['/inicioAdmin']);
        break;

      case 'ROLE_SOCIO':
        this.router.navigate(['/inicioSocio']);
        break;

      case 'ROLE_JUGADOR':
        this.router.navigate(['/inicioJugador']);
        break;

      default:
        this.router.navigate(['/inicio']);
    }
  }
}