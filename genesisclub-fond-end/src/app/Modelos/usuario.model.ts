// ==========================================
// INTERFACES DE USUARIO Y SOLICITUDES
// ==========================================

export interface SocioDTO {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  activo: boolean;
  fechaRegistro: string;
}

export interface JugadorDTO {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefonoContacto: string;
  activo: boolean;
  fechaRegistro: string;
  idEquipo?: number;
}

export interface AdminDTO {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  activo: boolean;
  fechaRegistro: string;
}

export interface SolicitudSocioDTO {
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  tipoDocumento?: string;
  numeroDocumento?: string;
}

export interface SolicitudJugadorDTO {
  nombre: string;
  apellido: string;
  email: string;
  telefonoContacto: string;
  posicion?: string;
  numeroJersey?: number;
}

export interface RegistroInvitadoDTO {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  telefono?: string;
}

export interface ResponceDTO {
  mensage: string;
  numOfErrors: number;
}
