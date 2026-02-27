import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  // Clonamos la petición para añadir el token si existe
  const authReq = token 
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) 
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      
      // Manejo de errores de autenticación/autorización
      if (error.status === 401 || error.status === 403) {
        
        // Aquí puedes usar: this.toast.error('Sesión expirada');
        alert('Tu sesión ha expirado o no tienes permisos suficientes.');
        
        localStorage.removeItem('token'); // Limpieza de seguridad
        router.navigate(['/login']);
      }

      // Devolvemos el error para que el componente también pueda manejarlo si quiere
      return throwError(() => error);
    })
  );
};