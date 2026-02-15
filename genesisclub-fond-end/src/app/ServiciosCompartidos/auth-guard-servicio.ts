import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthServicio } from './auth-servicio';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {

  constructor(
    private auth: AuthServicio,
    private router: Router
  ) {}

  canActivate(): boolean {

    if(this.auth.isLogged()){
      return true;
    }

    this.router.navigate(['/login']);
    return false;
  }
}
