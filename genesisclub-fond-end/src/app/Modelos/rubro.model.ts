export interface RubroDTO {
  id: number;
  nombre: string;
  descripcion: string;
  activo: boolean;
  fechaCreacion: string;
  fechaModificacion: string;
  idCreador: number;
  claveAcceso: string;
  fechaClaveGeneracion: string;
  claveActiva: boolean;
}

export interface RubroSocioDTO {
  id: number;
  fechaAsignacion: string;
  idSocio: number;
  nombreSocio: string;
  idRubro: number;
  nombreRubro: string;
}

export interface HistorialRubroDTO {
  id: number;
  accion: string;
  detalle: string;
  fecha: string;
  idAdmin: number;
  idRubro: number;
}

export interface UsuarioRubroDTO {
  id: number;
  activo: boolean;
  fechaIngreso: string;
  tipoUsuario: string;
  idRubro: number;
  idUsuario: number;
}

export interface RubroAccesoLogDTO {
  id: number;
  fechaAcceso: string;
  claveUtilizada: string;
  tipoUsuario: string;
  exitoso: boolean;
  ipAcceso: string;
  idRubro: number;
  nombreRubro: string;
  idUsuario: number;
  nomUsuario: string;
}
