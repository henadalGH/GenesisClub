# 📚 Documentación Completa del Módulo Rubros

## 📋 Resumen Ejecutivo

El módulo **Rubros** es un sistema completo de gestión de secciones/categorías en GenesisClub. Permite a los administradores crear, modificar y gestionar rubros, y a los socios solicitar acceso a ellos.

---

## 🏗️ Arquitectura General

### Stack Tecnológico

**Backend:**
- Spring Boot 3.x
- Spring Data JPA
- Spring Security (JWT)
- Base de datos relacional (MySQL/PostgreSQL)

**Frontend:**
- Angular 17+ (standalone components)
- RxJS (reactive programming)
- TypeScript 5+
- Reactive Forms

---

## 📦 Componentes Backend

### 1. Entidades (DTOs)

#### RubroDTO
```typescript
// 10 campos principales
- id: number
- nombre: string
- descripcion: string
- activo: boolean
- fechaCreacion: string (ISO 8601)
- fechaModificacion: string
- idCreador: number
- claveAcceso: string (hash)
- fechaClaveGeneracion: string
- claveActiva: boolean
```

#### RubroSocioDTO
Relación entre Rubro y Socio
```typescript
- id: number
- idRubro: number
- nombreRubro: string
- idSocio: number
- nombreSocio: string
- fechaAsignacion: string
```

#### UsuarioRubroDTO
Membresía de usuario en rubros
```typescript
- id: number
- idUsuario: number
- idRubro: number
- tipoUsuario: enum (SOCIO, JUGADOR, ADMIN)
- activo: boolean
- fechaIngreso: string
```

#### HistorialRubroDTO
Auditoría de cambios
```typescript
- id: number
- idRubro: number
- idAdmin: number
- accion: enum (CREAR, MODIFICAR, ELIMINAR, ACTIVAR, DESACTIVAR)
- detalle: string
- fecha: string
```

#### RubroAccesoLogDTO
Registro de accesos
```typescript
- id: number
- idRubro: number
- nombreRubro: string
- idUsuario: number
- nomUsuario: string
- tipoUsuario: enum
- claveUtilizada: string
- exitoso: boolean
- ipAcceso: string
- fechaAcceso: string
```

---

### 2. Repositorios

#### RubroRepository
```java
- findByActivo(boolean): List<RubroDTO>
- findByNombre(nombre: string): Optional<RubroDTO>
- findByClaveAcceso(clave: string): Optional<RubroDTO>
- findAllActivosOrdenados(): List<RubroDTO>
- buscarActivosPorNombre(nombre: string): List<RubroDTO>
```

#### RubroSocioRepository
```java
- findByRubroId(id: Long): List<RubroSocioDTO>
- findBySocioId(id: Long): List<RubroSocioDTO>
- findByRubroIdAndSocioId(rubroId, socioId): Optional<RubroSocioDTO>
- existsByRubroIdAndSocioId(rubroId, socioId): boolean
- deleteByRubroIdAndSocioId(rubroId, socioId): void
```

#### UsuarioRubroRepository
```java
- findByRubroId(id: Long): List<UsuarioRubroDTO>
- findByUsuarioId(id: Long): List<UsuarioRubroDTO>
- findByActivo(boolean): List<UsuarioRubroDTO>
- findUsuariosActivosPorRubro(rubroId): List<UsuarioRubroDTO>
- findRubrosActivosPorUsuario(usuarioId): List<UsuarioRubroDTO>
- existsByRubroIdAndUsuarioId(rubroId, usuarioId): boolean
```

#### HistorialRubroRepository
```java
- findByRubroId(id: Long): List<HistorialRubroDTO>
- findByAdminId(id: Long): List<HistorialRubroDTO>
- findHistorialPorRubro(rubroId): List<HistorialRubroDTO>
- findHistorialPorFechas(inicio, fin): List<HistorialRubroDTO>
```

#### RubroAccesoLogRepository
```java
- findByRubroId(id: Long): List<RubroAccesoLogDTO>
- findByUsuarioId(id: Long): List<RubroAccesoLogDTO>
- findByExitoso(boolean): List<RubroAccesoLogDTO>
- findAccesosPorRubro(rubroId): List<RubroAccesoLogDTO>
- findAccesosPorFecha(inicio, fin): List<RubroAccesoLogDTO>
- findAccesosExitosasPorRubro(rubroId): List<RubroAccesoLogDTO>
```

---

### 3. Servicios

#### RubroService (Interface)
```java
List<RubroDTO> obtenerTodos()
List<RubroDTO> obtenerActivos()
RubroDTO obtenerPorId(id)
RubroDTO obtenerPorNombre(nombre)
RubroDTO obtenerPorClaveAcceso(clave)
RubroDTO crear(RubroDTO)
RubroDTO actualizar(id, RubroDTO)
void eliminar(id)
List<RubroDTO> buscarPorNombre(termino)
RubroDTO activar(id)
RubroDTO desactivar(id)
```

#### RubroSocioService
```java
List<RubroSocioDTO> obtenerTodos()
RubroSocioDTO obtenerPorId(id)
List<RubroSocioDTO> obtenerPorRubro(rubroId)
List<RubroSocioDTO> obtenerPorSocio(socioId)
RubroSocioDTO crear(RubroSocioDTO)
RubroSocioDTO actualizar(id, RubroSocioDTO)
void eliminar(id)
boolean existeRelacion(rubroId, socioId)
void eliminarRelacion(rubroId, socioId)
```

#### UsuarioRubroService
```java
List<UsuarioRubroDTO> obtenerTodos()
UsuarioRubroDTO obtenerPorId(id)
UsuarioRubroDTO crear(UsuarioRubroDTO)
UsuarioRubroDTO actualizar(id, UsuarioRubroDTO)
void eliminar(id)
List<UsuarioRubroDTO> obtenerPorRubro(rubroId)
List<UsuarioRubroDTO> obtenerPorUsuario(usuarioId)
List<UsuarioRubroDTO> obtenerActivos()
List<UsuarioRubroDTO> obtenerUsuariosActivosPorRubro(rubroId)
List<UsuarioRubroDTO> obtenerRubrosActivosPorUsuario(usuarioId)
```

#### HistorialRubroService
```java
List<HistorialRubroDTO> obtenerTodos()
HistorialRubroDTO obtenerPorId(id)
List<HistorialRubroDTO> obtenerPorRubro(rubroId)
List<HistorialRubroDTO> obtenerPorAdmin(adminId)
HistorialRubroDTO crear(HistorialRubroDTO)
void eliminar(id)
List<HistorialRubroDTO> findHistorialPorFechas(inicio, fin)
```

#### RubroAccesoLogService
```java
List<RubroAccesoLogDTO> findAllRubroAccesoLog()
RubroAccesoLogDTO findById(id)
RubroAccesoLogDTO save(RubroAccesoLogDTO)
void deleteById(id)
List<RubroAccesoLogDTO> findByRubroId(id)
List<RubroAccesoLogDTO> findByUsuarioId(id)
List<RubroAccesoLogDTO> findByExitoso(boolean)
List<RubroAccesoLogDTO> findAccesosPorRubro(rubroId)
List<RubroAccesoLogDTO> findAccesosPorFecha(inicio, fin)
List<RubroAccesoLogDTO> findAccesosExitosasPorRubro(rubroId)
```

---

### 4. Controladores REST

#### RubroController
```
GET  /api/rubro                    - Obtener todos
GET  /api/rubro/activos            - Rubros activos (público)
GET  /api/rubro/{id}               - Por ID
GET  /api/rubro/nombre/{nombre}    - Por nombre (público)
GET  /api/rubro/clave/{clave}      - Por clave (público)
GET  /api/rubro/buscar/{termino}   - Búsqueda (público)
POST /api/rubro                    - Crear
PUT  /api/rubro/{id}               - Actualizar
DELETE /api/rubro/{id}             - Eliminar
PUT  /api/rubro/{id}/activar       - Activar
PUT  /api/rubro/{id}/desactivar    - Desactivar
```

#### RubroSocioController
```
GET  /api/rubro-socio              - Todos
GET  /api/rubro-socio/{id}         - Por ID
GET  /api/rubro-socio/rubro/{id}   - Por rubro
GET  /api/rubro-socio/socio/{id}   - Por socio
GET  /api/rubro-socio/existe       - Verificar existencia
POST /api/rubro-socio              - Crear
PUT  /api/rubro-socio/{id}         - Actualizar
DELETE /api/rubro-socio/{id}       - Eliminar
```

#### UsuarioRubroController
```
GET  /api/usuario-rubro            - Todos
GET  /api/usuario-rubro/{id}       - Por ID
POST /api/usuario-rubro            - Crear
PUT  /api/usuario-rubro/{id}       - Actualizar
DELETE /api/usuario-rubro/{id}     - Eliminar
```

#### HistorialRubroController
```
GET  /api/historial-rubro          - Todos
GET  /api/historial-rubro/{id}     - Por ID
GET  /api/historial-rubro/rubro/{id} - Por rubro
GET  /api/historial-rubro/admin/{id} - Por admin
GET  /api/historial-rubro/fechas   - Por rango de fechas
POST /api/historial-rubro          - Crear
DELETE /api/historial-rubro/{id}   - Eliminar
```

#### RubroAccesoLogController
```
GET  /api/rubro-acceso-log         - Todos
GET  /api/rubro-acceso-log/{id}    - Por ID
GET  /api/rubro-acceso-log/rubro/{id} - Por rubro
GET  /api/rubro-acceso-log/usuario/{id} - Por usuario
GET  /api/rubro-acceso-log/exitoso/{bool} - Por éxito
GET  /api/rubro-acceso-log/exitosos/{id} - Exitosos por rubro
GET  /api/rubro-acceso-log/fecha   - Por fecha
POST /api/rubro-acceso-log         - Crear
DELETE /api/rubro-acceso-log/{id}  - Eliminar
```

---

### 5. Configuración de Seguridad

#### SecurityConfig.java
Rutas configuradas:

**Públicas (sin autenticación):**
```
/api/rubro/activos
/api/rubro/buscar/**
/api/rubro/nombre/**
/api/rubro/clave/**
```

**Admin (ROLE_ADMIN):**
```
/api/rubro/**
/api/rubro-socio/**
/api/historial-rubro/**
/api/rubro-acceso-log/**
```

**Socio + Admin:**
```
/api/usuario-rubro/**
```

---

## 🎨 Componentes Frontend

### 1. Modelos TypeScript
**Archivo:** `src/app/Modelos/rubro.model.ts`

```typescript
export interface RubroDTO { ... }
export interface RubroSocioDTO { ... }
export interface HistorialRubroDTO { ... }
export interface UsuarioRubroDTO { ... }
export interface RubroAccesoLogDTO { ... }
```

### 2. Servicios Angular

#### RubroServicio
```typescript
obtenerTodos(): Observable<RubroDTO[]>
obtenerActivos(): Observable<RubroDTO[]>
obtenerPorId(id): Observable<RubroDTO>
obtenerPorNombre(nombre): Observable<RubroDTO>
obtenerPorClaveAcceso(clave): Observable<RubroDTO>
buscar(termino): Observable<RubroDTO[]>
crear(rubro): Observable<RubroDTO>
actualizar(id, rubro): Observable<RubroDTO>
eliminar(id): Observable<void>
activar(id): Observable<RubroDTO>
desactivar(id): Observable<RubroDTO>
```

#### RubroSocioServicio
```typescript
obtenerTodos(): Observable<RubroSocioDTO[]>
obtenerPorId(id): Observable<RubroSocioDTO>
obtenerPorRubro(id): Observable<RubroSocioDTO[]>
obtenerPorSocio(id): Observable<RubroSocioDTO[]>
crear(rs): Observable<RubroSocioDTO>
actualizar(id, rs): Observable<RubroSocioDTO>
eliminar(id): Observable<void>
```

#### UsuarioRubroServicio
```typescript
obtenerTodos(): Observable<UsuarioRubroDTO[]>
obtenerPorId(id): Observable<UsuarioRubroDTO>
crear(ur): Observable<UsuarioRubroDTO>
actualizar(id, ur): Observable<UsuarioRubroDTO>
eliminar(id): Observable<void>
```

#### HistorialRubroServicio
```typescript
obtenerTodos(): Observable<HistorialRubroDTO[]>
obtenerPorRubro(id): Observable<HistorialRubroDTO[]>
obtenerPorAdmin(id): Observable<HistorialRubroDTO[]>
crear(hr): Observable<HistorialRubroDTO>
```

#### RubroAccesoLogServicio
```typescript
obtenerTodos(): Observable<RubroAccesoLogDTO[]>
obtenerPorRubro(id): Observable<RubroAccesoLogDTO[]>
obtenerPorFecha(inicio, fin): Observable<RubroAccesoLogDTO[]>
crear(ral): Observable<RubroAccesoLogDTO>
```

### 3. Componentes

#### AdminListarRubros
**Ruta:** `/listaRubros`
**Auth:** ROLE_ADMIN

Funcionalidades:
- Listar todos los rubros
- Buscar por nombre
- Filtrar por estado (activo/inactivo)
- Editar rubro
- Activar/Desactivar
- Eliminar rubro

**Campos visibles:**
- ID
- Nombre
- Descripción
- Estado
- Fecha Creación
- Acciones

#### AdminCrearRubros
**Ruta:** `/crearRubros`
**Auth:** ROLE_ADMIN

Formulario reactivo con:
- Nombre (requerido)
- Descripción (requerido)
- Clave de Acceso (requerido)
- Activo (checkbox)
- Validación en tiempo real
- Mensajes de éxito/error

#### AdminVerRubro
**Ruta:** `/verRubro/:id`
**Auth:** ROLE_ADMIN

Funcionalidades:
- Ver detalles del rubro
- Modo edición con toggle
- Actualizar información
- Breadcrumb de navegación

#### SocioMisRubros
**Ruta:** `/mis-rubros`
**Auth:** ROLE_SOCIO

Funcionalidades:
- Ver rubros asignados (activos)
- Ver rubros disponibles
- Solicitar acceso a rubros
- Ver estado de membresía
- Filtrar por estado

---

## 🔄 Flujos de Usuario

### 1. Admin Creando un Rubro

```
/crearRubros
    ↓
Ingresa datos (nombre, descripción, clave)
    ↓
Hace clic en "Crear Rubro"
    ↓
Se guarda en BD
    ↓
Se registra en HistorialRubro (acción: CREAR)
    ↓
Se redirige a /listaRubros
    ↓
Aparece el nuevo rubro en la lista
```

### 2. Admin Modificando un Rubro

```
/listaRubros
    ↓
Admin elige editar un rubro
    ↓
/verRubro/:id
    ↓
Click en "Editar"
    ↓
Modo edición activado
    ↓
Modifica campos
    ↓
Click en "Guardar Cambios"
    ↓
Se actualiza en BD
    ↓
Se registra en HistorialRubro (acción: MODIFICAR)
    ↓
Modo vista activado
```

### 3. Socio Solicitando Acceso a Rubros

```
/mis-rubros
    ↓
Ve lista de rubros disponibles
    ↓
Click en "Solicitar Acceso"
    ↓
Se crea registro en usuario_rubro
    ↓
tipoUsuario = SOCIO, activo = false
    ↓
Se registra en HistorialRubro
    ↓
Admin lo aprueba (actualiza activo = true)
    ↓
Socio ve rubro en "Mis Rubros Activos"
```

### 4. Auditoría de Accesos

Cada vez que se accede a un rubro con clave:
```
Usuario intenta acceso
    ↓
Se valida clave de acceso
    ↓
Se registra en rubro_acceso_log
    ↓
exitoso = true/false
    ↓
ipAcceso guardada
    ↓
Disponible para auditoría
```

---

## 📊 Base de Datos

### Tablas Principales

```sql
-- Rubros
CREATE TABLE rubros (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) UNIQUE NOT NULL,
    descripcion TEXT NOT NULL,
    activo BOOLEAN DEFAULT true,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_creador BIGINT NOT NULL,
    clave_acceso VARCHAR(255),
    fecha_clave_generacion DATETIME,
    clave_activa BOOLEAN DEFAULT true,
    FOREIGN KEY (id_creador) REFERENCES usuarios(id)
);

-- Rubro - Socio
CREATE TABLE rubro_socio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_rubro BIGINT NOT NULL,
    id_socio BIGINT NOT NULL,
    fecha_asignacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY (id_rubro, id_socio),
    FOREIGN KEY (id_rubro) REFERENCES rubros(id),
    FOREIGN KEY (id_socio) REFERENCES socios(id)
);

-- Usuario - Rubro
CREATE TABLE usuario_rubro (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_usuario BIGINT NOT NULL,
    id_rubro BIGINT NOT NULL,
    tipo_usuario ENUM('SOCIO', 'JUGADOR', 'ADMIN'),
    activo BOOLEAN DEFAULT true,
    fecha_ingreso DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY (id_usuario, id_rubro),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_rubro) REFERENCES rubros(id)
);

-- Historial Rubro
CREATE TABLE historial_rubro (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_rubro BIGINT NOT NULL,
    id_admin BIGINT NOT NULL,
    accion ENUM('CREAR', 'MODIFICAR', 'ELIMINAR', 'ACTIVAR', 'DESACTIVAR'),
    detalle TEXT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rubro) REFERENCES rubros(id),
    FOREIGN KEY (id_admin) REFERENCES usuarios(id)
);

-- Acceso Log
CREATE TABLE rubro_acceso_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_rubro BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    tipo_usuario ENUM('SOCIO', 'JUGADOR', 'ADMIN'),
    clave_utilizada VARCHAR(255),
    exitoso BOOLEAN,
    ip_acceso VARCHAR(45),
    fecha_acceso DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rubro) REFERENCES rubros(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);
```

---

## 🔐 Seguridad

### Principios Implementados

1. **Autenticación JWT**
   - Token basado en roles
   - Renovación automática

2. **Autorización RBAC**
   - ROLE_ADMIN: Control total
   - ROLE_SOCIO: Acceso limitado a secciones
   - Público: Solo lectura de rubros activos

3. **Cifrado de Claves**
   - BCryptPasswordEncoder para claves de acceso
   - Validación en cada acceso

4. **Auditoría**
   - Registro de todas las acciones
   - IP del usuario
   - Timestamp
   - Usuario que realizó la acción

5. **Rate Limiting**
   - Implementado en SecurityConfig
   - Previene ataques de fuerza bruta

---

## 🚀 Uso y Ejemplos

### 1. Crear un Rubro (Backend)

```java
// Llamada al servicio
POST /api/rubro
Content-Type: application/json

{
  "nombre": "Fútbol",
  "descripcion": "Sección para actividades de fútbol",
  "claveAcceso": "futbol123",
  "activo": true,
  "idCreador": 1
}

// Respuesta
{
  "id": 1,
  "nombre": "Fútbol",
  "descripcion": "Sección para actividades de fútbol",
  "activo": true,
  "claveAcceso": "[hash]",
  "claveActiva": true,
  ...
}
```

### 2. Listar Rubros (Frontend)

```typescript
// En el componente
export class ListarRubros implements OnInit {
  rubros: RubroDTO[] = [];

  constructor(private rubroServicio: RubroServicio) {}

  ngOnInit() {
    this.rubroServicio.obtenerTodos().subscribe(data => {
      this.rubros = data;
    });
  }

  buscar(termino: string) {
    this.rubroServicio.buscar(termino).subscribe(data => {
      this.rubros = data;
    });
  }
}
```

### 3. Validar Clave de Acceso

```typescript
// En el servicio
validarAcceso(rubroId: number, clave: string): Observable<boolean> {
  return this.http.post<{exitoso: boolean}>(
    `${this.url}/${rubroId}/validar-clave`,
    { clave }
  ).pipe(
    map(res => res.exitoso),
    tap(exitoso => {
      // Registrar acceso
      if (exitoso) {
        this.registrarAcceso(rubroId, clave);
      }
    })
  );
}
```

---

## 📈 Estadísticas y Reportes

### Métricas Disponibles

1. **Rubros**
   - Total de rubros
   - Activos vs Inactivos
   - Rubros por mes
   - Más populares

2. **Membresías**
   - Usuarios por rubro
   - Tasa de membresía
   - Nuevas membresías por período

3. **Accesos**
   - Total de intentos
   - Tasa de éxito
   - Intentos fallidos
   - Usuarios más activos

4. **Auditoría**
   - Cambios realizados
   - Admin más activo
   - Acciones por tipo

---

## 🐛 Troubleshooting

### Problema: No puede crear rubro

**Posibles causas:**
- Usuario no tiene rol ROLE_ADMIN
- Nombre de rubro duplicado
- Validación fallida

**Solución:**
```java
// Verificar en SecurityConfig
if (!userHasRole("ROLE_ADMIN")) {
    throw new AccessDeniedException();
}
```

### Problema: Clave de acceso no funciona

**Causa:** Clave no coincide con BCrypt hash

**Solución:**
```java
// En el servicio
boolean validarClave(String claveIngresada, String claveHasheada) {
    return passwordEncoder.matches(claveIngresada, claveHasheada);
}
```

### Problema: Socios no ven sus rubros

**Causa:** Estado no está en activo (activo = false)

**Solución:**
```typescript
// En el servicio
obtenerRubrosActivosPorUsuario(usuarioId) {
    return this.http.get(
        `${this.url}/usuario/${usuarioId}/activos`
    );
}
```

---

## 📞 Soporte

Para reportar bugs o sugerir features:
1. Ir a Issues
2. Descripción detallada
3. Pasos para reproducir
4. Stack trace si aplica

---

## 📄 Licencia

Esta documentación es parte del proyecto GenesisClub.

**Versión:** 1.0.0
**Última actualización:** 2024
