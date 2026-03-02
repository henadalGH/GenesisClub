import { Component, OnInit, inject } from '@angular/core'; // Usamos inject para ser más modernos
import { AuthServicio } from '../../ServiciosCompartidos/auth-servicio';
import { RouterLink } from "@angular/router";
import { InvitacionServicio } from '../../ServicioAdministrador/invitacion-servicio';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-herader-socio',
  standalone: true,
  imports: [RouterLink, FormsModule], // Limpio, sin CommonModule
  templateUrl: './herader-socio.html',
  styleUrl: './herader-socio.css',
})
export class HeraderSocio implements OnInit {
  // Inyecciones modernas con inject()
  private authServicio = inject(AuthServicio);
  private invitacionServicio = inject(InvitacionServicio);

  modalAbierto = false;
  emailInvitado = '';
  cargando = false;

  ngOnInit(): void {
    const id = this.authServicio.getUserId();
    console.log('ID socio activo:', id);
  }

  logout() {
    this.authServicio.logout();
  }

  abrirModal() {
    this.modalAbierto = true;
    this.emailInvitado = '';
  }

  cerrarModal() {
    this.modalAbierto = false;
  }

  enviarInvitacion() {
    const socioId = this.authServicio.getUserId();

    if (!socioId || !this.emailInvitado) return;

    this.cargando = true;

    this.invitacionServicio.crearInvitacion(socioId, this.emailInvitado).subscribe({
      next: (res) => {
        alert('Invitación enviada correctamente.');
        this.cerrarModal();
        this.cargando = false;
      },
      error: (err) => {
        this.cargando = false;
        alert(err.error?.mensage || 'Error al enviar invitación');
      }
    });
  }
}