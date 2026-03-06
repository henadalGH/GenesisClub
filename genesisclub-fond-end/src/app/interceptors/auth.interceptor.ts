import { HttpInterceptorFn } from "@angular/common/http";


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

<<<<<<< HEAD
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
=======
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(authReq); // 🚀 SIN catchError
>>>>>>> ImplemntacionMail
};