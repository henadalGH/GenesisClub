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

export const routes: Routes = [

    //Rutas Compartidas
    {path:"inicio", component: Inicio},
    {path:"login", component: Login},
    {path: "solicitud", component: SolicitudSocio},
    {path:'', redirectTo: '/inicio', pathMatch: 'full'},

    //Rutas Socios
    {path:"inicioSocio", component: InicioSocio}, 
    
    //Rutas jugador
    {path:"inicioJugador", component: IncioJugador},

    //Rutas del administrador
    {path: "inicioAdmin", component: InicioAdmin},
    {path: "headerAdmin", component: HeaderAdmin},
    {path: "invitaciones", component: Invitaciones},
    {path:"solicitudesPendientes", component: SolicitudesPendientes},
    {path:"listaSocios", component: ListaSocio}
];
