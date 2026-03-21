# 📋 Sistema de Solicitudes Pendientes - Documentación

## Descripción General

Sistema completo de gestión de solicitudes pendientes para GenesisClub, que incluye:
- Solicitudes de Socios
- Solicitudes de Jugadores  
- Solicitudes de Rubros

## 🏗️ Arquitectura

### Estructura de Carpetas

```
genesisclub-fond-end/src/app/
├── Administrador/
│   ├── solicitudes-pendientes/              (Socios)
│   ├── solicitudes-jugadores-pendientes/    (Jugadores)
│   └── solicitudes-rubros-pendientes/       (Rubros)
├── ComponentesCompartidos/
│   ├── modal-confirmacion/                  (Modal reutilizable)
│   └── notificaciones/                      (Notificaciones Toast)
├── ServicioAdministrador/
│   ├── pendientes-servicios.ts              (HTTP Socios)
│   ├── jugador-pendientes-servicios.ts      (HTTP Jugadores)
│   └── solicitud-rubro-servicio.ts          (HTTP Rubros)
├── ServiciosCompartidos/
│   └── notificaciones-servicio.ts           (Gestión de notificaciones)
├── Modelos/
│   └── usuario.model.ts                     (DTOs tipados)
└── styles/
    └── solicitudes-compartido.css           (Estilos compartidos)
```

## 📦 Componentes Principales

### 1. **Modal de Confirmación** (`ModalConfirmacionComponent`)

Componente reutilizable para confirmar acciones críticas.

**Propiedades:**
- `visible: boolean` - Muestra/oculta el modal
- `titulo: string` - Título del modal
- `mensaje: string` - Mensaje de confirmación
- `textoBtnConfirmar: string` - Texto del botón confirmar
- `textoBtnCancelar: string` - Texto del botón cancelar
- `tipoAccion: 'aceptar' | 'rechazar' | 'peligro'` - Estilo del botón
- `cargando: boolean` - Muestra estado de carga

**Eventos:**
- `@Output() confirmar` - Se emite al confirmar
- `@Output() cancelar` - Se emite al cancelar

**Ubicación:** `ComponentesCompartidos/modal-confirmacion/`

---

### 2. **Notificaciones Toast** (`NotificacionesComponent`)

Muestra notificaciones no intrusivas en la esquina superior derecha.

**Métodos del servicio:**
```typescript
notificacionesServicio.exito(mensaje, duracion)
notificacionesServicio.error(mensaje, duracion)
notificacionesServicio.advertencia(mensaje, duracion)
notificacionesServicio.info(mensaje, duracion)
```

**Ubicación:** `ComponentesCompartidos/notificaciones/`

---

### 3. **Componente Solicitudes Socios**

**Funcionalidades:**
✅ Listar todas las solicitudes de socios pendientes  
✅ Aceptar/Rechazar con confirmación modal  
✅ Notificaciones automáticas  
✅ Tipado fuerte con DTOs  
✅ Unsubscribe automático al destruir  

**Ubicación:** `Administrador/solicitudes-pendientes/`

---

### 4. **Componente Solicitudes Jugadores**

Idéntica funcionalidad a Socios pero para jugadores.

**Ubicación:** `Administrador/solicitudes-jugadores-pendientes/`

---

### 5. **Componente Solicitudes Rubros**

**Funcionalidades adicionales:**
✅ Vista en tabla (más compacta)  
✅ Información enfatizada del rubro  
✅ Manejo robusto de errores  
✅ Debug logging integrado  

**Ubicación:** `Administrador/solicitudes-rubros-pendientes/`

---

## 🔄 Flujo de Datos

```
Componente
    ↓
Modal de Confirmación (Usuario confirma)
    ↓
Servicio HTTP (PUT /actualizar, /aprobar, /rechazar)
    ↓
Backend
    ↓
Respuesta exitosa
    ↓
Filtrar del array + Notificación + Modal cierra
```

## 💾 Modelos de Datos

### SolicitudSocioDTO
```typescript
{
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
```

### SolicitudJugadorDTO
```typescript
{
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
```

### SolicitudRubroDTO
```typescript
{
  id: number;
  socio: { id, nombreUsuario, emailUsuario };
  rubro: { id, nombre, descripcion };
  estado: 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';
  fechaCreacion: string;
}
```

## 🎨 Estilos

### Paleta de Colores
- **Primario:** `#007bff` (Azul)
- **Éxito:** `#4caf50` (Verde)
- **Rechazo:** `#ff9800` (Naranja)
- **Peligro:** `#f44336` (Rojo)

### Responsive
✅ Desktop (1200px+)  
✅ Tablet (768px - 1199px)  
✅ Móvil (480px - 767px)  
✅ Mini (< 480px)  

## 🚀 Cómo Usar

### 1. **En un componente, inyecta los servicios:**
```typescript
constructor(
  private solicitudService: SolicitudRubroServicio,
  private notificacionesServicio: NotificacionesServicio
) {}
```

### 2. **Carga las solicitudes:**
```typescript
cargarSolicitudes(): void {
  this.solicitudService.obtenerSolicitudesPendientes()
    .subscribe({
      next: (data) => this.solicitudes = data,
      error: (err) => this.notificacionesServicio.error(err.message)
    });
}
```

### 3. **Abre el modal:**
```typescript
abrirModalAprobar(id: number): void {
  this.modalState = { visible: true, id, accion: 'aprobar' };
}
```

### 4. **Confirma y procesa:**
```typescript
confirmarAccion(): void {
  this.solicitudService.aprobarSolicitud(this.modalState.id!)
    .subscribe({
      next: () => {
        this.notificacionesServicio.exito('Aprobado');
        this.solicitudes = this.solicitudes.filter(s => s.id !== id);
      }
    });
}
```

## ⚠️ Manejo de Errores

El sistema contempla:
- **Errores de conexión (0, -1)** → Banner de error
- **401 Unauthorized** → Sesión expirada
- **403 Forbidden** → Permisos insuficientes
- **404 Not Found** → Recurso no encontrado
- **5xx Server Error** → Error del servidor

Todos se comunican vía:
✅ Notificación toast  
✅ Console.error() para debugging  
✅ ErrorMessage en template  

## 📱 Breakpoints Responsive

- **Desktop:** `1200px+`
- **Tablet:** `768px - 1199px`
- **Mobile:** `480px - 767px`
- **Mobile pequeño:** `< 480px`

## 🔐 Seguridad

✅ Tipado fuerte TypeScript  
✅ Validación en el template  
✅ Protección contra múltiples clics (bandera `cargando`)  
✅ Confirmación modal antes de acciones destructivas  
✅ Unsubscribe en OnDestroy para evitar memory leaks  

## 📊 Estado del Sistema

**Componentes listos:**
- ✅ Modal de confirmación
- ✅ Notificaciones
- ✅ Socios
- ✅ Jugadores
- ✅ Rubros
- ✅ Servicios tipados
- ✅ DTOs completos

**Características:**
- ✅ Aceptar/Rechazar con confirmación
- ✅ Notificaciones toast
- ✅ Tipado fuerte
- ✅ Responsive design
- ✅ Unsubscribe automático
- ✅ Manejo robusto de errores

## 🐛 Debugging

Habilita logs en console:
- `console.log('Respuesta:', data)`
- `console.error('Error:', error)`

El componente de Rubros incluye `debugToken()` para verificar autenticación.

## 📝 Próximas Mejoras

- [ ] Buscar/filtrar por nombre
- [ ] Ordenar por fecha
- [ ] Paginación
- [ ] Exportar a CSV
- [ ] Comentarios/notas al rechazar
- [ ] Historial de decisiones
- [ ] Bulk actions (aceptar/rechazar múltiples)

---

**Versión:** 1.0.0  
**Última actualización:** 21 de Marzo 2026  
**Status:** ✅ Producción
