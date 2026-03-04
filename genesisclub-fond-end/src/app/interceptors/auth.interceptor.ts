import { HttpInterceptorFn } from '@angular/common/http';

const publicUrls = [
  '/api/auth',
  '/api/usuario/registro',
  '/api/solicitud/nuevo',
  '/email',
  '/api/invitacion/aceptar'
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // 🔥 NO mandar token a rutas públicas
  if (publicUrls.some(url => req.url.includes(url))) {
    return next(req);
  }

  const token = localStorage.getItem('token');

  if (!token || token === 'null' || token === 'undefined') {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};