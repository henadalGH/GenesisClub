import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificacionesServicio, Notificacion } from '../../ServiciosCompartidos/notificaciones-servicio';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notificaciones',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notificaciones.html',
  styleUrl: './notificaciones.css'
})
export class NotificacionesComponent implements OnInit, OnDestroy {
  notificaciones: Notificacion[] = [];
  private subscription: Subscription | undefined;

  constructor(private notificacionesServicio: NotificacionesServicio) {}

  ngOnInit(): void {
    this.subscription = this.notificacionesServicio.notificaciones.subscribe(
      notificaciones => this.notificaciones = notificaciones
    );
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  getIcono(tipo: string): string {
    const iconos: { [key: string]: string } = {
      exito: '✓',
      error: '✕',
      advertencia: '!',
      info: 'i'
    };
    return iconos[tipo] || 'i';
  }

  remover(id: string): void {
    this.notificacionesServicio.remover(id);
  }
}
