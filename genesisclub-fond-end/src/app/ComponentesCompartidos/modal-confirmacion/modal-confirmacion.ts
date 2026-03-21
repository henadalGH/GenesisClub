import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal-confirmacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-confirmacion.html',
  styleUrl: './modal-confirmacion.css'
})
export class ModalConfirmacionComponent {
  @Input() visible: boolean = false;
  @Input() titulo: string = 'Confirmar acción';
  @Input() mensaje: string = '¿Estás seguro de que deseas continuar?';
  @Input() textoBtnConfirmar: string = 'Confirmar';
  @Input() textoBtnCancelar: string = 'Cancelar';
  @Input() tipoAccion: 'aceptar' | 'rechazar' | 'peligro' = 'peligro';
  @Input() cargando: boolean = false;

  @Output() confirmar = new EventEmitter<void>();
  @Output() cancelar = new EventEmitter<void>();

  onConfirmar(): void {
    if (!this.cargando) {
      this.confirmar.emit();
    }
  }

  onCancelar(): void {
    if (!this.cargando) {
      this.cancelar.emit();
    }
  }

  getClasetBtn(): string {
    return `btn btn-${this.tipoAccion}`;
  }
}
