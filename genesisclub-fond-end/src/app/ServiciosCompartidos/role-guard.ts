import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthServicio } from './auth-servicio';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {

  constructor(
    private auth: AuthServicio,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {

    const expectedRole = route.data['role'];
    const userRole = this.auth.getRol();

    if(userRole === expectedRole){
      return true;
    }

    this.router.navigate(['/inicio']);
    return false;
  }
}
