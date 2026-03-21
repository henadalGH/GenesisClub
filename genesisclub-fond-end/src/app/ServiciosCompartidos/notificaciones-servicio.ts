import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Notificacion {
  id: string;
  tipo: 'exito' | 'error' | 'advertencia' | 'info';
  mensaje: string;
  duracion?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificacionesServicio {
  private notificaciones$ = new BehaviorSubject<Notificacion[]>([]);
  public notificaciones = this.notificaciones$.asObservable();

  constructor() {}

  mostrar(tipo: 'exito' | 'error' | 'advertencia' | 'info', mensaje: string, duracion: number = 4000): void {
    const id = `${Date.now()}-${Math.random()}`;
    const notificacion: Notificacion = { id, tipo, mensaje, duracion };

    // Agregar notificación
    const actuales = this.notificaciones$.value;
    this.notificaciones$.next([...actuales, notificacion]);

    // Remover automáticamente después de la duración
    if (duracion > 0) {
      setTimeout(() => {
        this.remover(id);
      }, duracion);
    }
  }

  exito(mensaje: string, duracion?: number): void {
    this.mostrar('exito', mensaje, duracion);
  }

  error(mensaje: string, duracion?: number): void {
    this.mostrar('error', mensaje, duracion || 5000);
  }

  advertencia(mensaje: string, duracion?: number): void {
    this.mostrar('advertencia', mensaje, duracion);
  }

  info(mensaje: string, duracion?: number): void {
    this.mostrar('info', mensaje, duracion);
  }

  remover(id: string): void {
    const actuales = this.notificaciones$.value;
    this.notificaciones$.next(actuales.filter(n => n.id !== id));
  }

  limpiar(): void {
    this.notificaciones$.next([]);
  }
}
