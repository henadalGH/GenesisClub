import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css', // CORREGIDO
})
export class Login {

  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private authServicio: AuthServicio,
    private router: Router
  ) {}

  login() {
    // reset previous error
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Complete los campos';
      return;
    }

    this.authServicio.login(this.email, this.password).subscribe({
      next: (resp: any) => {
        // Redirige según rol usando el método del servicio
        this.authServicio.redirectByRole();
      },
      error: (err: any) => {
        // show inline message instead of alert
        if (err?.message && !err.message.startsWith('Http failure response')) {
          this.errorMessage = err.message;
        } else if (err?.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Usuario o contraseña incorrectos';
        }
      }
    });
  }
}
