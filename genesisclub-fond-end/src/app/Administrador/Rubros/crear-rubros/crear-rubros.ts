import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HeaderAdmin } from "../../header-admin/header-admin";
import { RubroDTO } from '../../../Modelos/rubro.model';
import { RubroServicio } from '../../../ServiciosCompartidos/rubro-servicio';

@Component({
  selector: 'app-crear-rubros',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, HeaderAdmin],
  templateUrl: './crear-rubros.html',
  styleUrl: './crear-rubros.css'
})
export class CrearRubros {
  rubroForm: FormGroup;
  loading: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';

  constructor(
    private fb: FormBuilder,
    private rubroServicio: RubroServicio,
    private router: Router
  ) {
    this.rubroForm = this.fb.group({
      nombre: ['', Validators.required],
      descripcion: ['', Validators.required],
      claveAcceso: ['', Validators.required],
      activo: [true]
    });
  }

  isFieldInvalid(field: string): boolean {
    const fieldControl = this.rubroForm.get(field);
    return !!(fieldControl && fieldControl.invalid && (fieldControl.dirty || fieldControl.touched));
  }

  guardar(): void {
    if (this.rubroForm.invalid) {
      this.mensajeError = 'Por favor completa todos los campos requeridos';
      return;
    }

    this.loading = true;
    const rubro: RubroDTO = {
      id: 0,
      nombre: this.rubroForm.value.nombre,
      descripcion: this.rubroForm.value.descripcion,
      claveAcceso: this.rubroForm.value.claveAcceso,
      activo: this.rubroForm.value.activo,
      fechaCreacion: new Date().toISOString(),
      fechaModificacion: new Date().toISOString(),
      idCreador: 0,
      fechaClaveGeneracion: new Date().toISOString(),
      claveActiva: true
    };

    this.rubroServicio.crear(rubro).subscribe({
      next: (data) => {
        this.loading = false;
        this.mensajeExito = `✅ Rubro '${data.nombre}' creado exitosamente`;
        this.rubroForm.reset({ activo: true });
        setTimeout(() => this.router.navigate(['/listaRubros']), 2000);
      },
      error: (err) => {
        this.loading = false;
        this.mensajeError = err.error?.message || 'Error al crear el rubro';
      }
    });
  }
}
