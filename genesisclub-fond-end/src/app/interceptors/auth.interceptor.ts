import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const url = new URL(req.url);
  const path = url.pathname;

  const publicPaths = [
    '/api/auth',
    '/api/usuario/registro',
    '/api/solicitud/nuevo',
    '/email',
    '/api/invitacion/aceptar'
  ];

  // 🔥 NO mandar token a públicas
  if (publicPaths.some(p => path.startsWith(p))) {
    return next(req);
  }

  const token = localStorage.getItem('token');

  if (!token || token === 'null' || token === 'undefined') {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
  );
};