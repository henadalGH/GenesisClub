import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; // 1. Importar ChangeDetectorRef
import { HeaderAdmin } from "../header-admin/header-admin";
import { SocioServicio } from '../../ServicioAdministrador/socio-servicio';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common'; // Recomendado para evitar problemas con directivas

@Component({
  selector: 'app-lista-socio',
  standalone: true,
  imports: [HeaderAdmin, CommonModule],
  templateUrl: './lista-socio.html',
  styleUrl: './lista-socio.css',
})
export class ListaSocio implements OnInit {

  socios: any[] = [];

  constructor(
    private socioServicio: SocioServicio,
    private router: Router,
    private cdr: ChangeDetectorRef // 2. Inyectar el detector
  ) {}

  ngOnInit() {
    this.obtenerSocio();
  }

  obtenerSocio() {
    this.socioServicio.obtenerSocio().subscribe({
      next: (respuesta: any) => {
        // 3. Usar el spread operator para crear una nueva referencia de memoria
        this.socios = [...respuesta];
        
        // 4. Forzar a Angular a que pinte los cambios en el HTML inmediatamente
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener socios:', err);
      }
    });
  }

  verSocio(id: number){
    this.router.navigate(['/verEmpleado',id]);
  }
}