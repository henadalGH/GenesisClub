import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HeaderAdmin } from "../../header-admin/header-admin";
import { RubroDTO } from '../../../Modelos/rubro.model';
import { RubroServicio } from '../../../ServiciosCompartidos/rubro-servicio';

@Component({
  selector: 'app-listar-rubros',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, HeaderAdmin],
  templateUrl: './listar-rubros.html',
  styleUrl: './listar-rubros.css'
})
export class ListarRubros implements OnInit {
  rubros: RubroDTO[] = [];
  rubrosFiltrados: RubroDTO[] = [];
  searchTerm: string = '';
  filtroEstado: string = '';

  constructor(private rubroServicio: RubroServicio) {}

  ngOnInit(): void {
    this.cargarRubros();
  }

  cargarRubros(): void {
    this.rubroServicio.obtenerTodos().subscribe({
      next: (data) => {
        this.rubros = data;
        this.rubrosFiltrados = data;
      },
      error: (err) => console.error('Error cargando rubros:', err)
    });
  }

  buscar(): void {
    if (this.searchTerm.trim() === '') {
      this.rubrosFiltrados = this.rubros;
    } else {
      this.rubroServicio.buscar(this.searchTerm).subscribe({
        next: (data) => this.rubrosFiltrados = data,
        error: (err) => console.error('Error buscando:', err)
      });
    }
  }

  filtrar(): void {
    if (this.filtroEstado === '') {
      this.rubrosFiltrados = this.rubros;
    } else if (this.filtroEstado === 'activo') {
      this.rubrosFiltrados = this.rubros.filter(r => r.activo);
    } else {
      this.rubrosFiltrados = this.rubros.filter(r => !r.activo);
    }
  }

  editar(id: number): void {
    console.log('Editar rubro:', id);
  }

  toggleActivo(id: number, activo: boolean): void {
    const accion = activo ? 
      this.rubroServicio.desactivar(id) : 
      this.rubroServicio.activar(id);

    accion.subscribe({
      next: () => {
        this.cargarRubros();
        console.log('Estado actualizado');
      },
      error: (err) => console.error('Error actualizando estado:', err)
    });
  }

  eliminar(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este rubro?')) {
      this.rubroServicio.eliminar(id).subscribe({
        next: () => {
          this.cargarRubros();
          console.log('Rubro eliminado');
        },
        error: (err) => console.error('Error eliminando rubro:', err)
      });
    }
  }
}
