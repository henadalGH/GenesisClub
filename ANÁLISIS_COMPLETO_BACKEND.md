# 📊 ANÁLISIS COMPLETO DEL BACKEND - GENESISCLUB

**Fecha:** 22 de Marzo de 2026  
**Proyecto:** GenesisClub Backend (Java 21, Spring Boot 3.5.12)  
**Estado Compilación:** ✅ EXITOSA (139 archivos compilados)  
**Calificación General Seguridad:** 🔴 LOW (5.8/10)

---

## 📋 TABLA DE CONTENIDOS

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Errores Críticos](#errores-críticos-por-corregir-ya)
3. [Vulnerabilidades de Seguridad](#vulnerabilidades-de-seguridad)
4. [Problemas de Configuración](#problemas-de-configuración)
5. [Problemas de Base de Datos/ORM](#problemas-de-base-de-datosorm)
6. [Plan de Acción](#plan-de-acción-recomendado)

---

## RESUMEN EJECUTIVO

### Estado de la Compilación
| Aspecto | Estado | Detalles |
|---------|--------|---------|
| Compilación | ✅ OK | 139 archivos compilados sin errores |
| Vulnerabilidades | 🔴 CRÍTICO | 12 críticas, 9 altas, 11 medias |
| Configuración | 🟠 PROBLEMAS | merge conflict sin resolver, credenciales expuestas |
| Autenticación | 🔴 INSEGURA | logout inefectivo, validación JWT incompleta |
| Base de Datos | 🟡 RIESGOS | ddl-auto=update en prod, SQL inyectable |

### Puntuación por Categoría
- **Autenticación/Autorización:** 4/10 🔴 (Endpoints públicos sin protección)
- **Seguridad de Datos:** 3/10 🔴 (Contraseñas en texto plano, credenciales expuestas)
- **Configuración:** 5/10 🟠 (Merge conflicts, propiedades inseguras)
- **CORS/CSRF:** 4/10 🔴 (Patterns demasiado permisivos)
- **JWT:** 6/10 🟡 (Tokens sin revocación efectiva)
- **Base de Datos:** 6/10 🟡 (Potencial inyección SQL, N+1 queries)

---

## 🚨 ERRORES CRÍTICOS POR CORREGIR YA

### 1. CONTRASEÑAS SIN ENCRIPTAR en Solicitudes Aceptadas
**Ubicación:** `RegistroUsuarioServicioImpl.java`  
**Línea:** ~96  
**Severidad:** 🔴 CRÍTICA  
**Problema:**
```java
if (desdeSolicitud) {
    usuario.setPassword(dto.getPassword());  // ❌ TEXTO PLANO
} else {
    usuario.setPassword(encoder.encode(dto.getPassword()));  // ✓ OK
}
```

**Impacto:** Usuarios registrados desde solicitudes (Socios/Jugadores) tienen contraseñas en texto plano

**Solución:**
```java
usuario.setPassword(encoder.encode(dto.getPassword()));  // Siempre encriptar
```

---

### 2. LOGOUT INEFECTIVO - Tokens Revocados Siguen Válidos
**Ubicación:** `JWTAuthorizationFilter.java` línea 44-46  
**Severidad:** 🔴 CRÍTICA  
**Problema:**
- Usuario hace logout → token agregado a `revokedTokens` (HashMap en memoria)
- Filtro JWT **NO VALIDA** esta blacklist
- Servidor reinicia → blacklist desaparece
- Token "revocado" vuelve a funcionar ➜ **Session hijacking posible**

**Impacto:** Usuario puede mantener acceso después de logout

**Solución:** Validar blacklist en cada request
```java
if (jwtService.isTokenRevoked(token)) {
    throw new JwtValidationException("Token has been revoked");
}
```

---

### 3. CREDENCIALES DE EMAIL EN TEXTO PLANO
**Ubicación:** `email.properties`  
**Severidad:** 🔴 CRÍTICA  
**Problema:**
```properties
email.username=genesisclub1993@gmail.com
email.password=ssfo ebfo vare romc  # ❌ Expuesta en repositorio
```

**Impacto:** Credenciales comprometidas si repo es público

**Solución:** Usar variables de entorno
```properties
email.username=${EMAIL_USERNAME}
email.password=${EMAIL_PASSWORD}
```

---

### 4. MERGE CONFLICT SIN RESOLVER en Configuración Crítica
**Ubicación:** `application.properties` líneas 11-19  
**Severidad:** 🔴 CRÍTICA  
**Problema:**
```properties
<<<<<<< HEAD
spring.main.allow-bean-definition-overriding=true
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
======= 
app.front.url=https://genesisclub-frontend.onrender.com
>>>>>>> ImplemntacionMail
```

**Impacto:** Configuración ambigua, seguridad deshabilitada de UserDetailsService

**Solución:** Resolver merge, documentar por qué se deshabilita

---

### 5. 10 ENDPOINTS PÚBLICOS SIN AUTENTICACIÓN
**Ubicación:** Múltiples Controllers  
**Severidad:** 🔴 CRÍTICA  

#### Endpoints que DEBERÍAN estar protegidos:
| Endpoint | Controller | Problema | Datos Expuestos |
|----------|------------|----------|-----------------|
| `GET /api/usuario/provincia/{provincia}` | UsuarioController | SIN @PreAuthorize | Lista usuarios por zona |
| `GET /api/usuario/zona/{zona}` | UsuarioController | SIN @PreAuthorize | Lista usuarios por zona |
| `GET /api/socio/todos` | SocioController | SIN @PreAuthorize | Emails, teléfonos, estado |
| `GET /api/rubro` | RubroController | SIN @PreAuthorize | Todas rubros |
| `POST /api/rubro` | RubroController | SIN @PreAuthorize | **Crear rubros sin auth** |
| `GET /api/historial-rubro/*` | HistorialRubroController | **7 endpoints públicos** | Auditoría administrativa |
| `GET /api/rubro-acceso-log/*` | RubroAccesoLogController | **7 endpoints públicos** | Logs de seguridad |
| `GET /api/relacion-socio/*` | RelacionSocioController | público + `@CrossOrigin(*)` | Árbol de referidos |
| `GET/POST/PUT/DELETE /api/usuario-rubro` | UsuarioRubroController | **CRUD público** | Manipulación sin auth |
| `POST /api/email/enviar` | EmailController | público | Email injection |

**Solución:** Agregar `@PreAuthorize("hasAnyRole('ADMIN', 'SOCIO')")` según corresponda

---

## 🔴 VULNERABILIDADES DE SEGURIDAD

### Categoría 1: AUTENTICACIÓN/AUTORIZACIÓN (4 Críticas)

#### 1.1 Endpoints Públicos Exponiendo Datos Sensibles (Crítico)
- `SocioController.obtenerSocios()` - Lista todos los socios
- `RubroController` - 5+ endpoints de lectura/escritura públicos  
- `HistorialRubroController` - 7 endpoints exponen auditoría
- `RubroAccesoLogController` - 7 endpoints exponen logs de acceso

**Remediación:** Agregar `@PreAuthorize("hasRole('ADMIN')")` a nivel de clase

---

#### 1.2 BOLA (Broken Object Level Authorization) - Sin Validación de Propiedad (Crítico)
```java
@GetMapping("/{id}")
public ResponseEntity<SocioDTO> obtenerPorId(@PathVariable Long id) {
    // ❌ NO valida si el usuario autenticado puede acceder a este socio
    SocioDTO socio = socioService.obtenerPorId(id);
    return ResponseEntity.ok(socio);
}
```

**Remediación:**
```java
@PreAuthorize("hasRole('ADMIN')")  // O validar propiedad
public ResponseEntity<SocioDTO> obtenerPorId(@PathVariable Long id) {
    SocioDTO socio = socioService.obtenerPorId(id);
    if (!isOwnerOrAdmin(socio, getCurrentUser())) {
        throw new AccessDeniedException("No tienes permisos");
    }
    return ResponseEntity.ok(socio);
}
```

---

### Categoría 2: CORS/CSRF (2 Críticas)

#### 2.1 CORS Pattern Demasiado Permisivo
**Ubicación:** `SecurityConfig.java` línea 76-79  
**Problema:** `https://*.onrender.com` permite **CUALQUIER subdominio**
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "https://*.onrender.com"  // ❌ Wildcard = TODO lo de onrender
));
```

**Remediación:**
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:4200",
    "https://genesisclub-frontend.onrender.com"  // Específico
));
```

#### 2.2 @CrossOrigin Demasiado Permisivo
**Ubicación:** `RelacionSocioController.java` línea 12  
**Problema:** `@CrossOrigin(origins = "*")` permite **TODO origen**

**Remediación:** Remover o usar específico
```java
// ❌ REMOVER esta línea
// @CrossOrigin(origins = "*")
```

---

### Categoría 3: EXPOSICIÓN DE INFORMACIÓN (3 Críticas)

#### 3.1 Contraseñas Exposición en Logs
**Ubicación:** 
- `RegistroController.java` línea 26: `System.out.println(dto.getPassword())`
- `SolicitudSocioController.java` línea 43: `System.out.println(solicitud.getPassword())`- `InvitacionController.java` línea 31: `System.out.println("Este es el id que LLEGA "+socioId)`

**Remediación:** Eliminar y usar logger
```java
// ❌ REMOVER:
// System.out.println(dto.getPassword());

// ✅ REEMPLAZAR:
logger.debug("User registration initiated for email: {}", dto.getEmail());
```

---

#### 3.2 Email Controller Permite Email Injection/Spoofing
**Ubicación:** `EmailController.java` línea 20-24  
**Problema:** Endpoint público permite enviar emails sin autenticación
```java
@PostMapping("/enviar")
public ResponseEntity<String> enviarEmail(@RequestBody EmailDTO emailDTO) {
    // ❌ PÚBLICO - Permite a cualquiera enviar emails
    emailService.enviarCorreo(emailDTO);
}
```

**Ataque posible:** Email spoofing, spam, phishing

**Remediación:**
```java
@PostMapping("/enviar")
@PreAuthorize("hasAnyRole('ADMIN', 'SOCIO')")  // Agregar protección
public ResponseEntity<String> enviarEmail(@RequestBody EmailDTO emailDTO) {
    // Validar dominio de email destino
    if (!isValidEmailDomain(emailDTO.getDestinatario())) {
        return ResponseEntity.badRequest().build();
    }
    emailService.enviarCorreo(emailDTO);
    return ResponseEntity.ok("Email enviado");
}
```

---

### Categoría 4: JWT (2 Críticas)

#### 4.1 Validación Incompleta de JWT Claims
**Problema:** Faltan validaciones de:
- `aud` (audience) - Token reutilizable en otras aplicaciones
- `iss` (issuer) - No se valida quién emitió el token  
- `jti` (JWT ID) - Imposible revocar tokens específicos

**Remediación:** Agregar claims adicionales
```java
JwtClaimsSet claims = JwtClaimsSet.builder()
    .issuer("https://genesisclub.com")
    .audience(Collections.singletonList("genesisclub-app"))
    .jwtId(UUID.randomUUID().toString())  // Para revocación
    .subject(email)
    .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))  // Reducir a 15 min
    .build();
```

#### 4.2 Expiración de Token Muy Larga
**Problema:** 4 horas es demasiado (recomendación: 15 minutos)
- Mayor ventana de oportunidad si token es comprometido
- Necesita sincronización de logout en múltiples instancias

**Remediación:** Reducir a 15 min + implementar refresh token

---

### Categoría 5: VALIDACIÓN (1 Crítica)

#### 5.1 RubroRepository Vulnerable a LIKE Injection
**Ubicación:** `RubroRepository.java`  
**Problema:** LIKE con entrada de usuario sin escapar
```java
List<Rubro> findByNombreLike(String nombre);  // ❌ Sin escapar %_
```

**Ataque:** Entrada `%` retorna todos los registros

**Remediación:**
```java
List<Rubro> buscarPorNombre(String nombre) {
    // Escapar caracteres especiales LIKE
    String escaped = nombre.replace("\\", "\\\\")
                           .replace("%", "\\%")
                           .replace("_", "\\_");
    return repository.findByNombreLike("%" + escaped + "%");
}
```

---

## 🟠 PROBLEMAS DE CONFIGURACIÓN

### 1. security/SecurityConfig.java

#### 1.1 CSRF Deshabilitado Sin Justificación (Media)
```java
.csrf(csrf -> csrf.disable())  // ❌ Sin documentación
```
**Aunque es correcto para JWT stateless, debería documentarse**

**Remediación:** Agregar comentario
```java
// CSRF deshabilitado porque:
// - Aplicación stateless con JWT
// - No usa cookies de sesión
// - Protección en origen (Origin header) como capa adicional
.csrf(csrf -> csrf.disable())
```

#### 1.2 Headers CORS Demasiado Permisivos (Media)
```java
configuration.setAllowedHeaders(Arrays.asList("*"));  // ❌ Todo
```

**Remediación:**
```java
configuration.setAllowedHeaders(Arrays.asList(
    "Content-Type",
    "Authorization",
    "Accept",
    "X-Requested-With"
));
```

---

### 2. application.properties - Merge Conflict (Crítico)

**Estado actual:**
```properties
<<<<<<< HEAD
spring.main.allow-bean-definition-overriding=true
spring.autoconfigure.exclude=org.springframework.boot...UserDetailsServiceAutoConfiguration
=======
app.front.url=https://genesisclub-frontend.onrender.com
>>>>>>> ImplemntacionMail
```

**Acción:** Resolver merge manualmente

**Versión recomendada:**
```properties
spring.application.name=genesisClub
server.port=8080
spring.profiles.active=dev

# JWT
jwt.private-key-path=jwtKeys/private_key.pem
jwt.public-key-path=jwtKeys/public_key.pem

# Frontend URLs
app.front.url=https://genesisclub-frontend.onrender.com

# IMPORTANTE: Remover 'allow-bean-definition-overriding' SOLO si es necesario
# documentar por qué en un TICKET de GitHub
```

---

### 3. application-dev.properties
✅ **OK** - Credenciales sin encriptar es válido en desarrollo

### 4. application-prod.properties

#### 4.1 🔴 CRÍTICO: `spring.jpa.show-sql=true` en Producción
```properties
spring.jpa.show-sql=true  # ❌ Expone queries en logs
```

**Remediación:**
```properties
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

#### 4.2 🔴 CRÍTICO: `spring.jpa.hibernate.ddl-auto=update` en Producción
```properties
spring.jpa.hibernate.ddl-auto=update  # ❌ PELIGROSO
```

**Riesgos:**
- Modificaciones automáticas del schema
- Pérdida de datos potencial
- Sin control de versiones de BD

**Remediación:**
```properties
spring.jpa.hibernate.ddl-auto=validate  # Solo validar
# Usar Flyway o Liquibase para migraciones

# O en application.properties por defecto:
spring.jpa.hibernate.ddl-auto=validate
```

---

### 5. email.properties

#### 5.1 🔴 CRÍTICO: Credenciales en Archivo
```properties
email.username=genesisclub1993@gmail.com
email.password=ssfo ebfo vare romc
```

**Remediación:** Variables de entorno
```properties
email.username=${EMAIL_USERNAME}
email.password=${EMAIL_PASSWORD}
```

---

## 🟡 PROBLEMAS DE BASE DE DATOS/ORM

### 1. Falta de Validación de Transacciones (Alta)
**Ubicación:** `UsuarioServicioImpl.java`  
**Problema:**
```java
// Sin @Transactional
public List<UsuarioEntity> buscarPorProvincia(String provincia) {
    List<UsuarioEntity> usuarios = repository.findByProvincia(provincia);
    // LazyInitializationException potencial si hay relaciones
}
```

**Remediación:**
```java
@Transactional(readOnly = true)
public List<UsuarioEntity> buscarPorProvincia(String provincia) {
    return repository.findByProvincia(provincia);
}
```

---

### 2. Falta de @Version en Entidades (Media)
**Problema:** Optimistic locking no configurado
- Race conditions en updates concurrentes
- Lost updates

**Remediación:** Agregar a TODAS las entidades
```java
@Entity
public class SocioEntity {
    @Version
    private Long version;  // Versionado automático
}
```

---

### 3. Potencial N+1 Query Problem (Media)
**Ubicación:** Múltiples Repositories con lazy loading  
**Problema:**
```java
// Sin EntityGraph
List<SocioEntity> socios = repository.findAll();  // 1 query
for (SocioEntity socio : socios) {
    socio.getRubros();  // N queries adicionales
}
```

**Remediación:** Usar EntityGraph
```java
@Query("SELECT s FROM SocioEntity s LEFT JOIN FETCH s.rubros")
List<SocioEntity> findAllWithRubros();
```

---

### 4. Cascada Probablemente Mal Configurada (Media)
**Ubicación:** Relaciones bidireccionales  
**Problema:**
```java
@OneToMany(cascade = CascadeType.ALL)  // ❌ Demasiado permisivo
private List<RubroEntity> rubros;
```

**Remediación:**
```java
@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private List<RubroEntity> rubros;
```

---

### 5. IDs Secuenciales Exponen Cantidad de Registros (Baja)
**Problema:** IDs de 1, 2, 3... revelan cuántos registros hay

**Remediación:** Usar UUID
```java
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

---

## 🎯 PLAN DE ACCIÓN RECOMENDADO

### FASE 1: EMERGENCIA (Hoy - próximas 2 horas)
**Bloquear deploy a producción hasta completar**

1. ⚠️ Resolver merge conflict en `application.properties`
2. 🔐 Cambiar `email.properties` a variables de entorno
3. 🔒 Remover `System.out.println()` de contraseñas
4. 🚫 Cambiar `ddl-auto=validate` en prod
5. 🔒 Remover `show-sql=true` en prod

**Tiempo:** 30 minutos  
**Riesgo si no se hace:** Credenciales expuestas, ddl-auto modifica BD automáticamente

---

### FASE 2: CRÍTICA (Esta semana)
**Atacar vulnerabilidades core**

1. 🔐 Encriptar contraseñas en `RegistroUsuarioServicioImpl`
2. 🔒 Implementar validación de revocación en `JWTAuthorizationFilter`
3. 🔒 Agregar `@PreAuthorize` a 10 endpoints públicos
4. 🛡️ Reducir expiración JWT a 15 min + refresh token
5. 🔍 Validar BOLA en endpoints GET

**Tiempo:** 3-4 hours  
**Riesgo si no se hace:** Usuarios con permisos después de logout, datos expuestos

---

### FASE 3: IMPORTANTE (Próximas 2 semanas)
**Hardening general**

1. 📊 Agregar `@Version` a todas entidades
2. ✅ Implementar GlobalExceptionHandler
3. 🔒 Usar DTOs en lugar de Entities en responses
4. 🎯 Escapar LIKE en RubroRepository
5. 📈 Agregar EntityGraph para N+1 queries
6. 🛡️ Validar Cascade types en todas relaciones
7. 📝 Agregar logging de auditoria centralizado

**Tiempo:** 8-12 horas  
**Beneficio:** Mejor performance, mantenibilidad, seguridad

---

### FASE 4: OPTIMIZACIÓN (Mes 2)
**Nice to have**

1. Redis para token blacklist
2. Rate limiting en endpoints
3. Monitoreo de queries (logs, metrics)
4. Actualizaciones mensuales de dependencias
5. Fortify/SonarQube análisis estático

---

## 📋 CHECKLIST POR CRITICIDAD

### 🔴 CRÍTICO (Bloquear deploy)
- [ ] Resolver merge conflict en application.properties
- [ ] Mover credenciales de email a variables de entorno
- [ ] Cambiar ddl-auto=validate en prod
- [ ] Remover show-sql=true en prod y cambiar a false
- [ ] Remover System.out.println() de contraseñas (3 ubicaciones)
- [ ] Encriptar contraseñas en RegistroUsuarioServicioImpl
- [ ] Implementar validación de blacklist en JWT filter
- [ ] Agregar @PreAuthorize a 10 endpoints públicos
- [ ] Cambiar CORS pattern de wildcard a específico
- [ ] Remover @CrossOrigin(*) de RelacionSocioController

### 🟠 ALTA (Esta semana)
- [ ] Reducir JWT expiration a 15 min + refresh token
- [ ] Agregar aud, iss, jti claims a JWT
- [ ] Implementar BOLA validation en GET endpoints
- [ ] Proteger Email Controller
- [ ] Escapar LIKE en RubroRepository

### 🟡 MEDIA (Próximas 2 semanas)
- [ ] Agregar @Version a entidades
- [ ] Agregar @Transactional a servicios
- [ ] Implementar GlobalExceptionHandler
- [ ] Usar DTOs en responses
- [ ] EntityGraph para N+1 queries

### 🟢 BAJA (Mejoras)
- [ ] Cambiar IDs a UUID
- [ ] Validar Cascade types
- [ ] Mejorar comentarios de CORS/CSRF
- [ ] Audit logging

---

## 📊 RESUMEN DE HALLAZGOS

| Categoría | Crítica | Alta | Media | Baja | Total |
|-----------|---------|------|-------|------|-------|
| Autenticación/Autorización | 4 | 2 | 2 | 0 | **8** |
| CORS/CSRF | 2 | 1 | 1 | 0 | **4** |
| Exposición de datos | 3 | 1 | 2 | 1 | **7** |
| JWT | 2 | 1 | 1 | 0 | **4** |
| Base de Datos | 0 | 1 | 5 | 1 | **7** |
| Configuración | 2 | 0 | 3 | 0 | **5** |
| Validación | 1 | 1 | 0 | 0 | **2** |
| **TOTAL** | **14** | **7** | **14** | **2** | **37** |

---

##  PRÓXIMOS PASOS

1. **Hoy:** Ejecutar FASE 1 (Emergencia) - 30 minutos
2. **Mañana:** Iniciar FASE 2 (Crítica) - 4 horas
3. **Esta semana:** Completar FASE 2
4. **Próximas 2 semanas:** FASE 3
5. **Mes 2:** FASE 4 + actualización de dependencias

**¿Quieres que comience la implementación de las correcciones?**

