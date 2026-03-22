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
  // 🔥 ID desde JWT - Versión Mejorada
  // ========================
  
  /**
   * Obtiene el ID del usuario (tabla usuario)
   * Almacenado en el claim 'sub' (subject) del JWT
   */
  getUserId(): number | null {
    const token = this.getToken();
    if (!token) return null;

    const decoded: any = jwtDecode(token);
    const rawId = decoded.sub || null;

    if (!rawId) return null;

    const parsedId = typeof rawId === 'number' ? rawId : parseInt(rawId, 10);
    return Number.isNaN(parsedId) ? null : parsedId;
  }

  /**
   * Obtiene el ID del socio (tabla socio) 
   * Almacenado en el claim 'socioId' del JWT
   * ⚠️ Solo disponible para usuarios con rol SOCIO
   */
  getSocioId(): number | null {
    const token = this.getToken();
    if (!token) return null;

    const decoded: any = jwtDecode(token);
    
    // El claim 'socioId' existe si el usuario tiene rol SOCIO
    const rawId = decoded.socioId || null;
    if (!rawId) return null;

    const parsedId = typeof rawId === 'number' ? rawId : parseInt(rawId, 10);
    return Number.isNaN(parsedId) ? null : parsedId;
  }

  /**
   * Obtiene el ID del jugador (tabla jugador)
   * Almacenado en el claim 'jugadorId' del JWT
   * ⚠️ Solo disponible para usuarios con rol JUGADOR
   */
  getJugadorId(): number | null {
    const token = this.getToken();
    if (!token) return null;

    const decoded: any = jwtDecode(token);
    
    // El claim 'jugadorId' existe si el usuario tiene rol JUGADOR
    const rawId = decoded.jugadorId || null;
    if (!rawId) return null;

    const parsedId = typeof rawId === 'number' ? rawId : parseInt(rawId, 10);
    return Number.isNaN(parsedId) ? null : parsedId;
  }

  /**
   * Obtiene el ID del admin (tabla admin)
   * Almacenado en el claim 'adminId' del JWT
   * ⚠️ Solo disponible para usuarios con rol ADMIN
   */
  getAdminId(): number | null {
    const token = this.getToken();
    if (!token) return null;

    const decoded: any = jwtDecode(token);
    
    // El claim 'adminId' existe si el usuario tiene rol ADMIN
    const rawId = decoded.adminId || null;
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
      console.log('🆔 User ID (tabla usuario):', this.getUserId());
      console.log('👥 Socio ID (tabla socio):', this.getSocioId());
      console.log('⚽ Jugador ID (tabla jugador):', this.getJugadorId());
      console.log('⚙️  Admin ID (tabla admin):', this.getAdminId());
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