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

// ==========================================
// DTOs PARA CREAR/ENVIAR SOLICITUDES
// ==========================================

export interface CrearSolicitudSocioDTO {
  nombre: string;
  apellido: string;
  email: string;
  codigoArea: string;
  numeroCelular: string;
  tipoDocumento?: string;
  numeroDocumento?: string;
  patente?: string;
  marca?: string;
  modelo?: string;
  anio?: number;
  tieneGnc?: boolean;
}

export interface CrearSolicitudJugadorDTO {
  nombre: string;
  apellido: string;
  email: string;
  codigoArea: string;
  numeroCelular: string;
  posicion?: string;
  numeroJersey?: number;
  patente?: string;
  marca?: string;
  modelo?: string;
  anio?: number;
  tieneGnc?: boolean;
}

// ==========================================
// DTOs PARA SOLICITUDES PENDIENTES (DEL BACKEND)
// ==========================================

export interface SolicitudSocioDTO {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  codigoArea: string;
  numeroCelular: string;
  tipoDocumento?: string;
  numeroDocumento?: string;
  patente?: string;
  estado: 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';
  fechaSolicitud: string;
  motivo?: string;
}

export interface SolicitudJugadorDTO {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  codigoArea: string;
  numeroCelular: string;
  posicion?: string;
  numeroJersey?: number;
  patente?: string;
  estado: 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';
  fechaSolicitud: string;
  motivo?: string;
}

export interface SolicitudRubroDTO {
  id: number;
  socio: {
    id: number;
    nombreUsuario: string;
    emailUsuario: string;
  };
  rubro: {
    id: number;
    nombre: string;
    descripcion: string;
  };
  estado: 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';
  fechaCreacion: string;
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
