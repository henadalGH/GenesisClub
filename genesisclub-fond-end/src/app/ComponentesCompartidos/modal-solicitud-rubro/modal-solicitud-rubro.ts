import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-modal-solicitud-rubro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-solicitud-rubro.html',
  styleUrl: './modal-solicitud-rubro.css'
})
export class ModalSolicitudRubroComponent {
  @Input() visible: boolean = false;
  @Input() rubroNombre: string = '';
  @Input() cargando: boolean = false;
  @Input() errorMessage: string = '';

  @Output() confirmar = new EventEmitter<string>();
  @Output() cancelar = new EventEmitter<void>();

  claveAcceso: string = '';

  onSolicitar(): void {
    if (!this.cargando && this.claveAcceso.trim()) {
      this.confirmar.emit(this.claveAcceso.trim());
      this.claveAcceso = '';
    }
  }

  onCancelar(): void {
    if (!this.cargando) {
      this.cancelar.emit();
      this.claveAcceso = '';
      this.errorMessage = '';
    }
  }
}