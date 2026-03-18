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

    // Prefer role-based IDs if present (e.g. socioId, jugadorId, adminId)
    const rol = this.getRol();

    let rawId: any = null;
    if (rol === 'ROLE_SOCIO') {
      rawId = decoded.socioId || decoded.socio?.id;
    } else if (rol === 'ROLE_JUGADOR') {
      rawId = decoded.jugadorId || decoded.jugador?.id;
    } else if (rol === 'ROLE_ADMIN') {
      rawId = decoded.adminId || decoded.admin?.id;
    }

    // Fallback to subject/id claims for backwards compatibility
    rawId = rawId || decoded.sub || decoded.id || null;

    if (!rawId) return null;

    const parsedId = typeof rawId === 'number' ? rawId : parseInt(rawId, 10);
    return Number.isNaN(parsedId) ? null : parsedId;
  }


  private hasToken(): boolean {
    return !!this.getToken();
  }


  // ========================
  // � DEBUG - INSPECCIONAR TOKEN
  // ========================
  debugToken(): void {
    const token = this.getToken();
    if (!token) {
      console.warn('❌ No hay token');
      return;
    }

    try {
      const decoded: any = jwtDecode(token);
      console.log('📋 Token Decodificado:', decoded);
      console.log('👤 Rol:', this.getRol());
      console.log('🆔 User ID (desde JWT):', this.getUserId());
      console.log('📧 Email:', localStorage.getItem('email'));
    } catch (e) {
      console.error('Error decodificando token:', e);
    }
  }


  // ========================
  // �🔁 REDIRECCIÓN POR ROL
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