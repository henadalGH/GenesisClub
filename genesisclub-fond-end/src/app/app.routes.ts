import { Routes } from '@angular/router';
import { Inicio } from './ComponentesPublico/inicio/inicio';
import { Login } from './ComponentesPublico/login/login';
import { SolicitudSocio } from './ComponentesPublico/solicitud-socio/solicitud-socio';
import { InicioSocio } from './Socio/inicio-socio/inicio-socio';
import { InicioAdmin } from './Administrador/inicio-admin/inicio-admin';
import { IncioJugador } from './Jugador/incio-jugador/incio-jugador';
import { HeaderAdmin } from './Administrador/header-admin/header-admin';
import { Invitaciones } from './Administrador/invitaciones/invitaciones';
import { SolicitudesPendientes } from './Administrador/solicitudes-pendientes/solicitudes-pendientes';
import { ListaSocio } from './Administrador/lista-socio/lista-socio';
import { InicioRubros } from './Rubros/inicio-rubros/inicio-rubros';
import { HeraderSocio } from './Socio/herader-socio/herader-socio';
import { InvitacionesSocio } from './Socio/invitaciones-socio/invitaciones-socio';
import { VerSocio } from './Administrador/ver-socio/ver-socio';

export const routes: Routes = [

  // Rutas públicas
  { path: '', redirectTo: '/inicio', pathMatch: 'full' },
  { path: 'inicio', component: Inicio },
  { path: 'login', component: Login },
  { path: 'solicitud', component: SolicitudSocio },
  { path: 'inicioRubro', component: InicioRubros },

  // Rutas Socios
  { path: 'inicioSocio', component: InicioSocio },
  { path: 'headerSocio', component: HeraderSocio },
  { path: 'invitaciones-socio', component: InvitacionesSocio },

  // Rutas Jugador
  { path: 'inicioJugador', component: IncioJugador },

  // Rutas Administrador
  { path: 'inicioAdmin', component: InicioAdmin },
  { path: 'headerAdmin', component: HeaderAdmin },
  { path: 'invitaciones-admin', component: Invitaciones },
  { path: 'solicitudesPendientes', component: SolicitudesPendientes },
  { path: 'listaSocios', component: ListaSocio },
  { path: 'verSocio/:id', component: VerSocio },

  // Ruta comodín (opcional)
  { path: '**', redirectTo: '/inicio' }
];