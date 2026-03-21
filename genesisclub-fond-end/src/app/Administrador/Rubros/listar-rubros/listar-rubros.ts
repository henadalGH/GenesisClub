import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
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

  constructor(
    private rubroServicio: RubroServicio,
    private cdr: ChangeDetectorRef // Fundamental para refrescar la vista
  ) {}

  ngOnInit(): void {
    this.cargarRubros();
  }

  // Carga inicial y refresco tras acciones
  cargarRubros(): void {
    this.rubroServicio.obtenerTodos().subscribe({
      next: (data) => {
        this.rubros = [...data];
        this.aplicarFiltrosLocales();
      },
      error: (err) => console.error('Error cargando rubros:', err)
    });
  }

  // Lógica central de filtrado (evita conflictos entre búsqueda y estado)
  aplicarFiltrosLocales(): void {
    let resultado = [...this.rubros];

    // Filtro por texto
    if (this.searchTerm.trim() !== '') {
      const term = this.searchTerm.toLowerCase();
      resultado = resultado.filter(r => 
        r.nombre?.toLowerCase().includes(term)
      );
    }

    // Filtro por estado
    if (this.filtroEstado === 'activo') {
      resultado = resultado.filter(r => r.activo);
    } else if (this.filtroEstado === 'inactivo') {
      resultado = resultado.filter(r => !r.activo);
    }

    this.rubrosFiltrados = resultado;
    
    // Forzamos a Angular a "pintar" los resultados ahora mismo
    this.cdr.detectChanges();
  }

  // Acción del botón buscar (usa el servicio para traer datos frescos)
  buscar(): void {
    if (this.searchTerm.trim() === '') {
      this.cargarRubros();
    } else {
      this.rubroServicio.buscar(this.searchTerm).subscribe({
        next: (data) => {
          this.rubrosFiltrados = [...data];
          this.cdr.detectChanges(); // Refresco inmediato post-búsqueda
        },
        error: (err) => console.error('Error buscando:', err)
      });
    }
  }

  // Acción del combo de filtros
  filtrar(): void {
    this.aplicarFiltrosLocales();
  }

  editar(id: number): void {
    console.log('Editar rubro:', id);
  }

  // Cambiar estado (Activar/Desactivar)
  toggleActivo(id: number, activo: boolean): void {
    const accion = activo ? 
      this.rubroServicio.desactivar(id) : 
      this.rubroServicio.activar(id);

    accion.subscribe({
      next: () => {
        // Al terminar la operación, recargamos la lista completa
        this.cargarRubros();
      },
      error: (err) => console.error('Error actualizando estado:', err)
    });
  }
}