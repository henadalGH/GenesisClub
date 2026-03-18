import { Routes } from '@angular/router';
import { Inicio } from './ComponentesPublico/inicio/inicio';
import { Login } from './ComponentesPublico/login/login';
import { SolicitudSocio } from './ComponentesPublico/solicitud-socio/solicitud-socio';
import { SolicitudJugador } from './ComponentesPublico/solicitud-jugador/solicitud-jugador';
import { InicioSocio } from './Socio/inicio-socio/inicio-socio';
import { InicioAdmin } from './Administrador/inicio-admin/inicio-admin';
import { IncioJugador } from './Jugador/incio-jugador/incio-jugador';
import { HeaderAdmin } from './Administrador/header-admin/header-admin';
import { Invitaciones } from './Administrador/invitaciones/invitaciones';
import { SolicitudesPendientes } from './Administrador/solicitudes-pendientes/solicitudes-pendientes';
import { SolicitudesJugadoresPendientes } from './Administrador/solicitudes-jugadores-pendientes/solicitudes-jugadores-pendientes';
import { ListaSocio } from './Administrador/lista-socio/lista-socio';
import { ListaJugador } from './Administrador/lista-jugador/lista-jugador';
import { VerJugador } from './Administrador/ver-jugador/ver-jugador';
import { InicioRubros } from './Rubros/inicio-rubros/inicio-rubros';
import { HeraderSocio } from './Socio/herader-socio/herader-socio';
import { InvitacionesSocio } from './Socio/invitaciones-socio/invitaciones-socio';
import { VerSocio } from './Administrador/ver-socio/ver-socio';
import { AuthGuard } from './ServiciosCompartidos/auth-guard-servicio';
import { RegistroInvitado } from './ComponentesPublico/registro-invitado/registro-invitado';
import { CrearRubros } from './Administrador/Rubros/crear-rubros/crear-rubros';
import { ListarRubros } from './Administrador/Rubros/listar-rubros/listar-rubros';
import { VerResPorSocio } from './Administrador/ver-res-por-socio/ver-res-por-socio';
import { VerRubro } from './Administrador/Rubros/ver-rubro/ver-rubro';
import { RubrosSocio } from './Socio/rubros-socio/rubros-socio';
import { MisSocios } from './Socio/mis-socios/mis-socios';
import { RubrosDisponibles } from './Socio/rubros-disponibles/rubros-disponibles';
import { SolicitarRubroComponent } from './Socio/solicitar-rubro/solicitar-rubro.component';
import { SolicitudesRubrosPendientesComponent } from './Administrador/solicitudes-rubros-pendientes/solicitudes-rubros-pendientes.component';


export const routes: Routes = [

  // =========================
  // 🌍 RUTAS PÚBLICAS
  // =========================
  { path: '', redirectTo: '/inicio', pathMatch: 'full' },
  { path: 'inicio', component: Inicio },
  { path: 'login', component: Login },
  { path: 'solicitud', component: SolicitudSocio },
  { path: 'solicitud-jugador', component: SolicitudJugador },
  { path: 'inicioRubro', component: InicioRubros },
  { path: "registroInvitado", component: RegistroInvitado},


  // =========================
  // 👤 RUTAS SOCIO
  // =========================
  { 
    path: 'inicioSocio',
    component: InicioSocio,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_SOCIO'] }
  },
  {
    path: 'headerSocio',
    component: HeraderSocio,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_SOCIO'] }
  },
  {
    path: 'invitaciones-socio',
    component: InvitacionesSocio,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_SOCIO'] }
  },
  {
    path: 'misRubros',
    component: RubrosSocio,
    
  },

  {path: 'misSocios',
    component: MisSocios
  },

  {path: 'rubrosDisponibles', component: RubrosDisponibles},

  {path: 'solicitarRubro/:id', component: SolicitarRubroComponent, canActivate: [AuthGuard], data: { role: ['ROLE_SOCIO'] }},


  // =========================
  // ⚽ RUTAS JUGADOR
  // =========================
  {
    path: 'inicioJugador',
    component: IncioJugador,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_JUGADOR'] }
  },


  // =========================
  // 🛠 RUTAS ADMIN
  // =========================
  {
    path: 'inicioAdmin',
    component: InicioAdmin,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'headerAdmin',
    component: HeaderAdmin,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'invitaciones-admin',
    component: Invitaciones,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'solicitudesPendientes',
    component: SolicitudesPendientes,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'solicitudesJugadoresPendientes',
    component: SolicitudesJugadoresPendientes,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'listaSocios',
    component: ListaSocio,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'verSocio/:id',
    component: VerSocio,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'listaJugadores',
    component: ListaJugador,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'verJugador/:id',
    component: VerJugador,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },

  // =========================
  // 🏁 RUTAS RUBROS
  // =========================
  {
    path: 'listaRubros',
    component: ListarRubros,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'crearRubros',
    component: CrearRubros,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'verRubro/:id',
    component: VerRubro,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  {
    path: 'solicitudesRubrosPendientes',
    component: SolicitudesRubrosPendientesComponent,
    canActivate: [AuthGuard],
    data: { role: ['ROLE_ADMIN'] }
  },
  { path: "verRedSocio/:id", component: VerResPorSocio },

  // =========================
  // ❌ COMODÍN
  // =========================
  { path: '**', redirectTo: '/inicio' }
];
