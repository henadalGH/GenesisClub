import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'], // CORREGIDO
})
export class Login {

  email: string = '';
  password: string = '';

  constructor(
    private authServicio: AuthServicio,
    private router: Router
  ) {}

  login() {
    if (!this.email || !this.password) {
      alert('Complete los campos');
      return;
    }

    this.authServicio.login(this.email, this.password).subscribe({
      next: (resp: any) => {
        // Redirige según rol usando el método del servicio
        this.authServicio.redirectByRole(resp.rol);
      },
      error: (err: any) => {
        alert(err.message || 'Usuario o contraseña incorrectos');
      }
    });
  }
}
