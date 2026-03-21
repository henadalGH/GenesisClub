import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HeaderAdmin } from "../../header-admin/header-admin";
import { RubroDTO } from '../../../Modelos/rubro.model';
import { RubroServicio } from '../../../ServiciosCompartidos/rubro-servicio';

@Component({
  selector: 'app-ver-rubro',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink, HeaderAdmin],
  templateUrl: './ver-rubro.html',
  styleUrl: './ver-rubro.css'
})
export class VerRubro implements OnInit {
  rubro: RubroDTO | null = null;
  rubroForm: FormGroup;
  loading: boolean = true;
  editando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  rubroId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private rubroServicio: RubroServicio,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.rubroForm = this.fb.group({
      nombre: ['', Validators.required],
      descripcion: ['', Validators.required],
      activo: [true]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.rubroId = +params['id'];
      this.cargarRubro();
    });
  }

  cargarRubro(): void {
    this.loading = true;
    this.rubroServicio.obtenerPorId(this.rubroId).subscribe({
      next: (data) => {
        this.rubro = data;
        this.rubroForm.patchValue({
          nombre: data.nombre,
          descripcion: data.descripcion,
          activo: data.activo
        });
        this.loading = false;
      },
      error: (err) => {
        this.mensajeError = 'Error al cargar el rubro';
        console.error('Error:', err);
        this.loading = false;
      }
    });
  }

  toggleEditar(): void {
    this.editando = !this.editando;
    if (!this.editando) {
      this.cargarRubro();
    }
  }

  guardarCambios(): void {
    if (this.rubroForm.invalid) {
      this.mensajeError = 'Por favor completa todos los campos';
      return;
    }

    const rubroActualizado: RubroDTO = {
      ...this.rubro!,
      nombre: this.rubroForm.value.nombre,
      descripcion: this.rubroForm.value.descripcion,
      activo: this.rubroForm.value.activo
    };

    this.rubroServicio.actualizar(this.rubroId, rubroActualizado).subscribe({
      next: (data) => {
        this.rubro = data;
        this.editando = false;
        this.mensajeExito = '✅ Rubro actualizado exitosamente';
      },
      error: (err) => {
        this.mensajeError = err.error?.message || 'Error al actualizar el rubro';
      }
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.rubroForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
