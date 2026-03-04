import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // ✅ MEJORA: Verificamos que el token exista Y que no sea el texto "null" o "undefined"
  if (!token || token === 'null' || token === 'undefined') {
    return next(req); // Envía la petición limpia
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};