import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SolicitudServicio } from '../../ServiciosCompartidos/solicitud-servicio';

@Component({
  selector: 'app-registro-invitado',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './registro-invitado.html',
  styleUrl: './registro-invitado.css'
})
export class RegistroInvitado implements OnInit {
  
  token: string | null = null;
  cargando = false;

  // Objeto para los datos del formulario
  datos = {
    nombre: '',
    apellido: '',
    dni: '',
    telefono: '',
    email: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private solicitudService: SolicitudServicio
  ) {}

  ngOnInit(): void {
    // Capturamos el token de la URL: ?token=xxxx
    this.token = this.route.snapshot.queryParamMap.get('token');
    
    // Si no hay token, lo mandamos al login por seguridad
    if (!this.token) {
      alert('Enlace de invitación no válido.');
      this.router.navigate(['/inicioSocio']);
    }
  }

  registrar() {
    if (!this.token) return;

    this.cargando = true;
    
    // Llamamos al servicio con los datos y el token
    this.solicitudService.registrarInvitado(this.datos, this.token).subscribe({
      next: (res: any) => {
        alert('¡Registro exitoso! Ya podés ingresar.');
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        this.cargando = false;
        alert(err.error?.mensage || 'Error al procesar el registro');
      }
    });
  }
}