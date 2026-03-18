import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SolicitudServicio } from '../../ServiciosCompartidos/solicitud-servicio';

@Component({
  selector: 'app-registro-invitado',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './registro-invitado.html',
  styleUrl: './registro-invitado.css' // corregido
})
export class RegistroInvitado implements OnInit {
  
  token: string | null = null;
  cargando = false;

  // Objeto para los datos del formulario, incluyendo password
  datos = {
    nombre: '',
    apellido: '',
    dni: '',
    telefono: '',
    email: '',
    password: ''   // ✨ importante para el registro
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private solicitudService: SolicitudServicio
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    
    if (!this.token) {
      alert('Enlace de invitación no válido.');
      this.router.navigate(['/inicioSocio']);
    }
  }

  registrar() {
    if (!this.token) return;

    if (!this.datos.password) {
      alert('Debes ingresar una contraseña');
      return;
    }

    this.cargando = true;

    this.solicitudService.registrarInvitado(this.datos, this.token).subscribe({
      next: (res) => {
        this.cargando = false;
        alert(res.mensage || '¡Registro exitoso! Ya podés ingresar.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.cargando = false;
        alert(err.error?.mensage || 'Error al procesar el registro');
      }
    });
  }
}