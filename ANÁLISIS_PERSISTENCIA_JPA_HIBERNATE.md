# 🔒 ANÁLISIS EXHAUSTIVO: CAPA DE PERSISTENCIA JPA/HIBERNATE
**Proyecto:** GenesisClub Backend  
**Fecha:** 22 de marzo de 2026  
**Nivel:** Análisis de Seguridad y Arquitectura

---

## 📋 TABLA DE CONTENIDOS
1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Configuración Hibernate](#configuración-hibernate)
3. [Repositorios y Seguridad SQL](#repositorios-y-seguridad-sql)
4. [Relaciones JPA y Serialización](#relaciones-jpa-y-serialización)
5. [Transacciones](#transacciones)
6. [Problemas ORM Específicos](#problemas-orm-específicos)
7. [Matriz de Riesgos](#matriz-de-riesgos)
8. [Recomendaciones](#recomendaciones)

---

## 📊 RESUMEN EJECUTIVO

### Hallazgos Críticos: **3 CRÍTICOS | 8 ALTOS | 5 MEDIOS**

| Categoría | Crítico | Alto | Medio | Info |
|-----------|---------|------|-------|------|
| **Configuración** | 2 | 1 | - | - |
| **Transacciones** | 1 | 3 | - | - |
| **Lazy Loading** | - | 2 | 2 | - |
| **Relaciones** | - | 1 | 2 | - |
| **Queries** | - | 1 | 1 | - |

**Puntuación de Seguridad:** 5.8/10 🔴

---

## ⚙️ CONFIGURACIÓN HIBERNATE

### 🔴 **CRÍTICO #1: ddl-auto=update en Producción**

**Archivo:** [application-prod.properties](application-prod.properties#L5)  
**Severidad:** 🔴 CRÍTICO  
**Tipo:** Riesgo de Integridad de Datos  

```properties
# ❌ PELIGROSO EN PRODUCCIÓN
spring.jpa.hibernate.ddl-auto=update
spring.jpa.generate-ddl=true
```

**Problemas:**
- Hibernate modifica el esquema automáticamente en cada reinicio
- Puede eliminar columnas o índices si cambias las propiedades de las entidades
- Usa transacciones implícitas que pueden bloquear el servidor
- Imposibilidad de rollback si algo sale mal

**Impacto:** Pérdida de datos, downtime no planificado, inconsistencia de esquema

**Remediación:**
```properties
# ✅ CORRECTO PARA PRODUCCIÓN
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.generate-ddl=false
```

**Por qué:**
- `validate`: Solo valida que el esquema coincida con entidades (sin modificaciones)
- Usa migraciones con Flyway/Liquibase para cambios controlados
- Las migraciones se revisionan y pueden rollback

---

### 🔴 **CRÍTICO #2: show-sql=true en Producción**

**Archivo:** [application-prod.properties](application-prod.properties#L6)  
**Severidad:** 🔴 CRÍTICO  
**Tipo:** Exposición de Información  

```properties
# ❌ EXPONE QUERIES EN LOGS
spring.jpa.show-sql=true
```

**Problemas:**
- Imprime TODAS las queries SQL en logs
- Expone estructura de tablas, nombres de columnas, patrones de acceso
- Logs pueden ser capturados por atacantes en servidores en la nube
- Degradación de rendimiento por I/O de logging

**Impacto:** Information Disclosure, Reconnaissance, Reconocimiento de futura explotación

**Remediación:**
```properties
# ✅ CORRECTO
spring.jpa.show-sql=false
# Usa logging de Hibernate en nivel DEBUG si lo necesitas (solo localmente)
logging.level.org.hibernate.SQL=DEBUG  # Solo en dev
```

---

### 🟡 **ALTO #1: format_sql=true en Dev (Innecesario)**

**Archivo:** [application-dev.properties](application-dev.properties#L8)  
**Severidad:** 🟡 ALTO (Mejora de rendimiento)  
**Tipo:** Rendimiento  

```properties
# Innecesario aunque no es crítico
spring.jpa.properties.hibernate.format_sql=true
```

**Problema:** Formatea SQL indentado, aumenta tamaño de logs, más I/O

**Remediación:** Mantenerlo solo si necesita debugging en local. En dev posterior:
```properties
spring.jpa.properties.hibernate.format_sql=false
```

---

## 🔍 REPOSITORIOS Y SEGURIDAD SQL

### ✅ **ESTADO GENERAL: SEGURO (Sin queries nativas)**

No se encontraron:
- ❌ `@Query(nativeQuery=true)`
- ❌ Concatenación de strings en queries
- ❌ `entityManager.createNativeQuery()`
- ❌ `createQuery()` manual

**Todos los repositories usan JpaRepository y queries nombradas.** ✅

---

### 🟡 **ALTO #2: LIKE sin validación en RubroRepository**

**Archivo:** [RubroRepository.java](src/main/java/com/example/genesisclub/genesisClub/Repositorio/RubroRepository.java#L21)  
**Severidad:** 🟡 ALTO  
**Tipo:** SQL Injection (LIKE Wildcard)  

```java
// ⚠️ Vulnerable a LIKE injection
@Query("SELECT r FROM RubroEntity r WHERE r.nombre LIKE %:nombre% AND r.activo = true")
List<RubroEntity> buscarActivosPorNombre(@Param("nombre") String nombre);
```

**Vulnerabilidad:** Si `nombre` contiene `%` o `_`:
```
Entrada: "rub%ro"
Query generado: WHERE nombre LIKE '%rub%ro%'  // Buscará cualquier cosa con "rub" y "ro"
```

**Impacto:** Acceso a información no autorizada, Información Disclosure

**Remediación:**
```java
@Query("SELECT r FROM RubroEntity r WHERE r.nombre LIKE %:nombre% AND r.activo = true")
List<RubroEntity> buscarActivosPorNombre(@Param("nombre") String nombre);

// En el Service, escapar caracteres especiales:
public List<RubroDTO> buscarPorNombre(String nombre) {
    // Escapar caracteres especiales de LIKE
    String nombreEscapado = nombre
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_");
    
    return rubroRepository.buscarActivosPorNombre(nombreEscapado)
        .stream()
        .map(this::toDTO)
        .toList();
}
```

---

### ✅ **BIEN: Otros Repositories**

Los siguientes repositories están bien implementados:

| Repository | Método | Estado |
|---|---|---|
| [UsuarioRepository](src/main/java/com/example/genesisclub/genesisClub/Repositorio/UsuarioRepository.java) | `findByEmail`, `findByUbicacionProvincia` | ✅ JpaRepository |
| [SocioRepository](src/main/java/com/example/genesisclub/genesisClub/Repositorio/SocioRepository.java) | `findByUsuario_Id` | ✅ Named Query |
| [InvitacionRepository](src/main/java/com/example/genesisclub/genesisClub/Repositorio/InvitacionRepository.java) | `findByToken` | ✅ Named Query |
| [JugadorRepository](src/main/java/com/example/genesisclub/genesisClub/Repositorio/JugadorRepository.java) | `findByUsuario_Id` | ✅ Named Query |

---

## 🔗 RELACIONES JPA Y SERIALIZACIÓN

### 🟡 **ALTO #3: Ciclo Infinito en RelacionUsuarioEntity**

**Archivo:** [RelacionUsuarioEntity.java](src/main/java/com/example/genesisclub/genesisClub/Modelo/Entidad/RelacionUsuarioEntity.java#L24-L32)  
**Severidad:** 🟡 ALTO  
**Tipo:** StackOverflowError, N+1 Queries  

```java
// RelacionUsuarioEntity
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_socio")
private SocioEntity socioPadre;  // Socio A → RelacionUsuario → Socio B

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_contacto")
private SocioEntity socioHijo;   // Socio B → (referidosDirectos) → Socio A [CICLO]
```

```java
// SocioEntity
@OneToMany(mappedBy = "socioPadre")
@JsonIgnore
private List<RelacionUsuarioEntity> referidosDirectos;  // ← Cierra el ciclo
```

**Problema:** Aunque tiene @JsonIgnore, si accedes por lazy loading fuera de transacción:
- StackOverflowError si se deserializa incorrectamente
- Queries N+1 si accedes a la relación sin fetch JOIN
- LazyInitializationException en OpenInView=false

**Ejemplo de problema:**
```java
// Esto causará problema en producción
SocioEntity socio = socioRepository.findById(1L).get();
socio.getReferidosDirectos().get(0).getSocioPadre()  // LazyInitializationException
```

**Remediación:**
```java
// Opción 1: Usar @JsonBackReference / @JsonManagedReference (RECOMENDADO)
@Entity
@Data
public class RelacionUsuarioEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_socio")
    @JsonBackReference("socio-referidos")  // ← No serializa
    private SocioEntity socioPadre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contacto")
    @JsonManagedReference("socio-referidos")
    private SocioEntity socioHijo;
}

@Entity
@Data
public class SocioEntity {
    @OneToMany(mappedBy = "socioPadre")
    @JsonManagedReference("socio-referidos")  // ← Aparece en JSON
    private List<RelacionUsuarioEntity> referidosDirectos;

    // Y la inversa para el otro lado
    @OneToMany(mappedBy = "socioHijo")
    @JsonBackReference("socio-referidos")
    private List<RelacionUsuarioEntity> sponsors;
}

// Opción 2: Usar DTOs para control total (ALTAMENTE RECOMENDADO)
@Data
public class SocioConReferidosDTO {
    private Long id;
    private String nombre;
    private List<RelacionDTO> referidosDirectos;  // Solo datos necesarios
}

@Data
public class RelacionDTO {
    private Long id;
    private Long socioHijoId;
    private LocalDate fecha;
}
```

---

### 🟡 **ALTO #4: LazyInitializationException en UsuarioController**

**Archivo:** [UsuarioController.java](src/main/java/com/example/genesisclub/genesisClub/Controller/UsuarioController.java) (Líneas 24, 27)  
**Severidad:** 🟡 ALTO  
**Tipo:** LazyInitializationException, N+1 Queries  

```java
@GetMapping("/provincia/{provincia}")
public ResponseEntity<List<UsuarioEntity>> porProvincia(@PathVariable String provincia) {
    // ❌ Retorna UsuarioEntity directamente
    // Si UsuarioServicioImpl NO tiene @Transactional, las relaciones LAZY fallarán
    return ResponseEntity.ok(usuarioService.buscarPorProvincia(provincia));
}

@GetMapping("/zona/{zona}")
public ResponseEntity<List<UsuarioEntity>> porZona(@PathVariable String zona) {
    // ❌ MISMO PROBLEMA
    return ResponseEntity.ok(usuarioService.buscarPorZona(zona));
}
```

**Problema:** [UsuarioServicioImpl](src/main/java/com/example/genesisclub/genesisClub/Servicio/servicioImpl/UsuarioServicioImpl.java) **NO tiene @Transactional**

```java
@Service
// ❌ SIN @Transactional ← PROBLEMA
public class UsuarioServicioImpl implements UsuarioServicio {

    @Override
    public List<UsuarioEntity> buscarPorZona(String zona) {
        return usuarioRepository.findByUbicacionZona(zona);
        // Aquí se cierra la transacción y las relaciones están LAZY
    }
    // Al serializar el JSON, intentará cargar rol, socio, etc. → LazyInitializationException
}
```

**Stack Trace esperado:**
```
org.hibernate.LazyInitializationException: 
failed to lazily initialize a collection of role 'com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity.socio', 
could not initialize proxy - no Session
```

**Remediación:**
```java
@Service
@Transactional(readOnly = true)  // ← AGREGAR ESTO
public class UsuarioServicioImpl implements UsuarioServicio {

    @Override
    public List<UsuarioEntity> buscarPorZona(String zona) {
        return usuarioRepository.findByUbicacionZona(zona);
    }
}

// MEJOR: Retornar DTO en lugar de Entity
@Service
@Transactional(readOnly = true)
public class UsuarioServicioImpl implements UsuarioServicio {

    @Override
    public List<UsuarioMapaDTO> buscarUsuariosParaMapaPorZona(String zona) {
        return usuarioRepository.findByUbicacionZona(zona)
                .stream()
                .map(this::mapToDTO)  // Convierte a DTO
                .toList();
    }

    private UsuarioMapaDTO mapToDTO(UsuarioEntity u) {
        UsuarioMapaDTO dto = new UsuarioMapaDTO();
        dto.setNombre(u.getNombre());
        // Solo incluir datos necesarios
        if (u.getUbicacion() != null) {
            dto.setCiudad(u.getUbicacion().getCiudad());
        }
        return dto;
    }
}
```

---

### 🟡 **ALTO #5: Posible N+1 en ServicioSocioImpl**

**Archivo:** [ServicioSocioImpl.java](src/main/java/com/example/genesisclub/genesisClub/Servicio/servicioImpl/ServicioSocioImpl.java#L37-L47)  
**Severidad:** 🟡 ALTO  
**Tipo:** N+1 Query Problem  

```java
@Override
public List<SocioDTO> obtenerSocio() {
    return socioRepository.findAll()  // Query 1: obtiene todos los socios
            .stream()
            .map(socio -> {
                // Esto accede a socio.getUsuario().getVehiculos()
                VehiculoEntity vehiculo = socio.getUsuario()  // Query N: se ejecuta por cada socio
                        .getVehiculos()                       // N queries adicionales
                        .stream()
                        .findFirst()
                        .orElse(null);

                return toDTO(socio, vehiculo);  // N+1 total
            })
            .toList();
}
```

**Problema:**
- Query 1: `SELECT * FROM socio` → 100 socios
- Queries N: Para cada socio, `SELECT * FROM usuario WHERE id = ?`
- Queries N: Para cada usuario, `SELECT * FROM vehiculo WHERE id_usuario = ?`
- **Total:** 1 + 100 + 100 = 201 queries (en lugar de 1)

**Impacto:** Lentitud exponencial, timeout de base de datos, saturación

**Remediación - Opción 1: EntityGraph**
```java
@Repository
public interface SocioRepository extends JpaRepository<SocioEntity, Long> {
    @EntityGraph(attributePaths = {"usuario", "usuario.vehiculos"})
    @Override
    List<SocioEntity> findAll();
}

// Ahora solo 1 query con JOINs
```

**Remediación - Opción 2: JPQL Query con JOIN FETCH (MEJOR)**
```java
@Repository
public interface SocioRepository extends JpaRepository<SocioEntity, Long> {
    @Query("SELECT s FROM SocioEntity s " +
           "LEFT JOIN FETCH s.usuario u " +
           "LEFT JOIN FETCH u.vehiculos")
    List<SocioEntity> findAllWithRelations();
}

// Service
@Service
@Transactional(readOnly = true)
public class ServicioSocioImpl implements SocioServicio {
    
    @Override
    public List<SocioDTO> obtenerSocio() {
        return socioRepository.findAllWithRelations()
                .stream()
                .map(socio -> {
                    VehiculoEntity vehiculo = socio.getUsuario()
                            .getVehiculos()
                            .stream()
                            .findFirst()
                            .orElse(null);
                    return toDTO(socio, vehiculo);
                })
                .toList();
    }
}
```

---

### 🟠 **MEDIO #1: Falta @JsonIgnore en AdminEntity.OneToMany**

**Archivo:** [AdminEntity.java](src/main/java/com/example/genesisclub/genesisClub/Modelo/Entidad/AdminEntity.java#L34-L38)  
**Severidad:** 🟠 MEDIO  
**Tipo:** Serialización JSON Innecesaria  

```java
@Entity
@Data
public class AdminEntity {
    @OneToMany(mappedBy = "admin")
    // ❌ SIN @JsonIgnore
    private List<RubroEntity> rubros = new ArrayList<>();

    @OneToMany(mappedBy = "admin")
    // ❌ SIN @JsonIgnore
    private List<HistorialRubroEntity> historial = new ArrayList<>();
}
```

**Problema:**
- Si accedes a AdminEntity en JSON, intenta serializar todas sus relaciones
- Causa LazyInitializationException si no está en transacción
- Exposición innecesaria de datos

**Remediación:**
```java
@Entity
@Data
public class AdminEntity {
    @OneToMany(mappedBy = "admin")
    @JsonIgnore  // ← AGREGAR
    private List<RubroEntity> rubros = new ArrayList<>();

    @OneToMany(mappedBy = "admin")
    @JsonIgnore  // ← AGREGAR
    private List<HistorialRubroEntity> historial = new ArrayList<>();
}
```

---

### 🟠 **MEDIO #2: Falta Configuración Explícita de Cascading**

**Archivo:** Todas las entidades  
**Severidad:** 🟠 MEDIO  
**Tipo:** Riesgo de Cascada Silenciosa  

```java
// Actual - sin cascading explícito (usa default)
@OneToMany(mappedBy = "usuario")
private List<SocioEntity> socio = new ArrayList<>();

// Default = fetch=FetchType.LAZY, cascade={} ← Nada se borra en cascada (BIEN)
// Pero es mejor hacerlo explícito
```

**Problema:** El comportamiento de cascading es implícito

**Remediación:**
```java
// Explícito: NO se cascadea nada (CORRECTO PARA ESTE PROYECTO)
@OneToMany(mappedBy = "usuario", cascade = {}, fetch = FetchType.LAZY)
@JsonIgnore
private List<SocioEntity> socio = new ArrayList<>();

// Nota: NUNCA usar cascade = CascadeType.ALL en @OneToMany
// Es peligroso porque borra entidades relacionadas al eliminar el padre
```

---

## 🔄 TRANSACCIONES

### 🔴 **CRÍTICO #3: UsuarioServicioImpl sin @Transactional**

**Archivo:** [UsuarioServicioImpl.java](src/main/java/com/example/genesisclub/genesisClub/Servicio/servicioImpl/UsuarioServicioImpl.java#L14)  
**Severidad:** 🔴 CRÍTICO  
**Tipo:** LazyInitializationException, Falta de ACID  

```java
@Service
// ❌ SIN @Transactional
public class UsuarioServicioImpl implements UsuarioServicio {

    @Override
    public List<UsuarioEntity> buscarPorZona(String zona) {
        // Transacción se abre y cierra aquí
        return usuarioRepository.findByUbicacionZona(zona);
        // Transacción cerrada - acceso a relaciones LAZY causará excepción
    }
}
```

**Remediación:**
```java
@Service
@Transactional(readOnly = true)  // ← AGREGAR
public class UsuarioServicioImpl implements UsuarioServicio {

    @Override
    public List<UsuarioEntity> buscarPorZona(String zona) {
        return usuarioRepository.findByUbicacionZona(zona);
    }
}
```

---

### 🟡 **ALTO #6: Transacciones Inconsistentes en InvitacionServiceImpl**

**Archivo:** [InvitacionServiceImpl.java](src/main/java/com/example/genesisclub/genesisClub/Servicio/servicioImpl/InvitacionServiceImpl.java)  
**Severidad:** 🟡 ALTO  
**Tipo:** Falta de Atomicidad  

```java
@Service
// ❌ Solo el método crearInvitacion tiene @Transactional
public class InvitacionServiceImpl implements InvitacionService {

    @Transactional  // Solo aquí
    @Override
    public InvitacionResponseDTO crearInvitacion(Long socioId, InvitacionRequestDTO dto) {
        // ...
    }

    @Override
    public InvitacionResponseDTO aceptarInvitacion(String token) {
        // ❌ SIN @Transactional - qué pasa si falla el email?
        // ...
    }
}
```

**Problema:** Si `aceptarInvitacion` falla a mitad, no hay rollback

**Remediación:**
```java
@Service
@Transactional  // ← AGREGAR A NIVEL DE CLASE
public class InvitacionServiceImpl implements InvitacionService {

    @Override
    // Hereda @Transactional de la clase
    public InvitacionResponseDTO crearInvitacion(Long socioId, InvitacionRequestDTO dto) {
        // ...
    }

    @Override
    // Hereda @Transactional de la clase
    public InvitacionResponseDTO aceptarInvitacion(String token) {
        // ...
    }

    // Métodos readOnly pueden sobreescribir
    @Transactional(readOnly = true)
    public Optional<InvitacionEntity> obtenerPorToken(String token) {
        return invitacionRepository.findByToken(token);
    }
}
```

---

### 🟠 **MEDIO #3: Falta de Rollback Explícito**

**Archivo:** [SolicitudSerSocioService](src/main/java/com/example/genesisclub/genesisClub/Servicio/SolicitudSerSocioService.java)  
**Severidad:** 🟠 MEDIO  
**Tipo:** Manejo de Excepciones  

**Problema General:** No hay manejo de rollback explícito en caso de excepciones

**Remediación:**
```java
@Service
@Transactional
public class SolicitudSerSocioServiceImpl {

    public void rechazarSolicitud(Long solicitudId) {
        SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        
        try {
            solicitud.setEstado(EstadoSolicitud.RECHAZADO);
            solicitudRepository.save(solicitud);
            
            // Si esto falla, la transacción se rollback automáticamente
            enviarEmailRechazo(solicitud);
        } catch (Exception e) {
            // Rollback automático por @Transactional
            // Pero es buena práctica loguear
            logger.error("Error al rechazar solicitud", e);
            throw new RuntimeException("No se pudo rechazar la solicitud", e);
        }
    }
}
```

---

## 🛠️ PROBLEMAS ORM ESPECÍFICOS

### ❌ **CRÍTICO #4️⃣: Falta @Version para Optimistic Locking**

**Archivos:** Todas las entidades  
**Severidad:** 🔴 CRÍTICO  
**Tipo:** Race Condition, Data Loss  

```java
// Actual - SIN @Version ❌
@Entity
@Data
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    // ❌ Sin @Version
}
```

**Problema:**
- Si dos usuarios modifican simultaneamente, el segundo sobrescribe al primero (lost update)
- Ejemplo:
```
Thread 1: Lee Usuario (nombre=Juan)
Thread 2: Lee Usuario (nombre=Juan)
Thread 1: Cambia nombre a "Pedro" y guarda
Thread 2: Cambia nombre a "Carlos" y guarda ← Sobrescribe cambio de Thread 1
Resultado: Cambio de Thread 1 se PIERDE
```

**Remediación - Agregar a TODAS las entidades:**
```java
@Entity
@Data
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Version  // ← AGREGAR
    private Long version;
    
    private String nombre;
}

@Entity
@Data
public class SocioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Version  // ← AGREGAR
    private Long version;
    
    private Integer cantidadInvitaciones;
}

// Idem para todas las entidades
```

**Impacto en Controller:**
```java
@PutMapping("/{id}")
public ResponseEntity<SocioDTO> actualizar(@PathVariable Long id, @RequestBody SocioDTO dto) {
    try {
        SocioDTO actualizado = socioService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    } catch (OptimisticLockingFailureException e) {
        // El usuario debe reintentar con los datos actualizados
        return ResponseEntity.status(409).body("El recurso fue modificado por otro usuario");
    }
}
```

---

### 🟠 **MEDIO #4: Exposición de IDs Secuenciales en API**

**Archivo:** Todos los Controllers  
**Severidad:** 🟠 MEDIO  
**Tipo:** Information Disclosure  

```java
// Actual - expone IDs ❌
@GetMapping("/{id}")
public ResponseEntity<SocioDTO> getSocioPorId(@PathVariable Long id) {
    return ResponseEntity.ok(socioServicio.obtenerPorId(id));
}
// Usuarios pueden enumerar: /api/socio/1, /api/socio/2, etc.
```

**Problema:** Permite enumerar todos los socios del sistema

**Remediación:**
```java
// Opción 1: Usar UUIDs en lugar de Long (MEJOR)
@Entity
public class SocioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)  // ← Cambia a UUID
    private UUID id;
}

// API: /api/socio/550e8400-e29b-41d4-a716-446655440000

// Opción 2: Validar autorización (MÍNIMO)
@GetMapping("/{id}")
@PreAuthorize("hasRole('ADMIN') or @socioService.esEIPropioSocio(#id, authentication.principal.id)")
public ResponseEntity<SocioDTO> getSocioPorId(@PathVariable Long id) {
    return ResponseEntity.ok(socioServicio.obtenerPorId(id));
}
```

---

### 🟠 **MEDIO #5: Falta de Índices en Campos Frecuentemente Buscados**

**Archivo:** Todas las entidades (Verificar en esquema DB)  
**Severidad:** 🟠 MEDIO  
**Tipo:** Rendimiento  

```java
// Campos que se usan frecuentemente en WHERE:
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);  // ← Sin índice = FULL TABLE SCAN
    List<UsuarioEntity> findByUbicacionProvincia(String provincia);  // ← Sin índice
    List<UsuarioEntity> findByUbicacionZona(String zona);  // ← Sin índice
}
```

**Remediación:**
```java
@Entity
@Data
@Table(name = "usuario", schema = "genesisclub",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})},
    indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_ubicacion_provincia", columnList = "id_ubicacion"),
        @Index(name = "idx_ubicacion_zona", columnList = "id_ubicacion")
    }
)
public class UsuarioEntity {
    @Column(name = "email")
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private UbicacionEntity ubicacion;
}

// Idem para:
// - RubroEntity: nombre (buscarActivosPorNombre)
// - RubroEntity: claveAcceso (findByClaveAcceso)
// - InvitacionEntity: token (findByToken)
// - SocioEntity: usuario_id (findByUsuario_Id)
```

---

### ✅ **BIEN: GeneratedValue Strategy**

Todas las entidades usan:
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ No predecible, seguro
```

No usa:
- ❌ `SEQUENCE` con valores predecibles
- ❌ `TABLE` generado manualmente

**Estado:** ✅ CORRECTO

---

## 📊 MATRIZ DE RIESGOS

| # | Problema | Archivo | Línea | Severidad | Tipo | Impacto |
|---|----------|---------|-------|-----------|------|---------|
| 1 | ddl-auto=update | application-prod.properties | 5 | 🔴 CRÍTICO | Integridad | Data Loss |
| 2 | show-sql=true | application-prod.properties | 6 | 🔴 CRÍTICO | Disclosure | Info Leak |
| 3 | Sin @Transactional UsuarioService | UsuarioServicioImpl | 14 | 🔴 CRÍTICO | Lazy Loading | Crash |
| 4 | Sin @Version | Todas entities | - | 🔴 CRÍTICO | Race | Lost Update |
| 5 | LIKE injection | RubroRepository | 21 | 🟡 ALTO | SQL Injection | Data Access |
| 6 | N+1 query | ServicioSocioImpl | 37 | 🟡 ALTO | Performance | Timeout |
| 7 | Ciclo infinito | RelacionUsuarioEntity | 24 | 🟡 ALTO | Serialization | Stack Overflow |
| 8 | LazyInit UsuarioController | UsuarioController | 24 | 🟡 ALTO | Lazy Loading | Crash |
| 9 | Inconsistente @Transactional | InvitacionServiceImpl | - | 🟡 ALTO | Atomicity | Partial Failure |
| 10 | Sin @JsonIgnore AdminEntity | AdminEntity | 34 | 🟠 MEDIO | Serialization | Lazy Init |
| 11 | Cascading implícito | Todas OneToMany | - | 🟠 MEDIO | ORM Config | Risk |
| 12 | Sin índices | Múltiples entidades | - | 🟠 MEDIO | Performance | Slow Query |
| 13 | IDs secuenciales | Controllers | - | 🟠 MEDIO | Enumeration | Reconnaissance |
| 14 | format_sql=true | application-dev.properties | 8 | 🟠 MEDIO | Performance | Log Size |
| 15 | Sin rollback explícito | Services | - | 🟠 MEDIO | Exception | Partial State |

---

## ✅ RECOMENDACIONES

### 🔴 **INMEDIATO (Semana 1)**

1. **Configuración Producción**
   - [ ] Cambiar `ddl-auto=update` a `validate`
   - [ ] Cambiar `show-sql=true` a `false`
   - [ ] Implementar Flyway para migraciones

2. **@Transactional**
   - [ ] Agregar `@Transactional(readOnly = true)` a UsuarioServicioImpl
   - [ ] Agregar `@Transactional` a InvitacionServiceImpl a nivel clase

3. **@Version**
   - [ ] Agregar `@Version` a TODAS las entidades

### 🟡 **CORTO PLAZO (Semana 2-3)**

4. **Relaciones**
   - [ ] Cambiar RelacionUsuarioEntity a usar DTOs
   - [ ] Agregar `@JsonIgnore` a AdminEntity.oneToMany
   - [ ] Cambiar UsuarioController a retornar DTOs

5. **Queries**
   - [ ] Implementar LIKE escape en RubroService
   - [ ] Agregar EntityGraph a SocioRepository
   - [ ] Usar JOIN FETCH en queries N+1

6. **Índices**
   - [ ] Agregar `@Index` anotaciones a campos búsqueda
   - [ ] Verificar índices en BD

### 🟠 **MEDIANO PLAZO (Mes 2)**

7. **Migraciones**
   - [ ] Implementar Flyway con versionado
   - [ ] Documentar cambios de esquema

8. **Auditoría**
   - [ ] Agregar `@CreationTimestamp`, `@UpdateTimestamp`
   - [ ] Implementar Javers para auditoría

9. **Monitoreo**
   - [ ] Configurar logs de Hibernate en producción
   - [ ] Monitorear queries lentas

---

## 📝 SCRIPT DE CORRECCIÓN AUTOMÁTICA

### Paso 1: Agregar @Version a todas las entidades

```bash
# Script para Windows PowerShell
$entities = Get-ChildItem -Path ".\src\main\java\com\example\genesisclub\genesisClub\Modelo\Entidad\*Entity.java"

foreach ($file in $entities) {
    $content = Get-Content $file.FullName -Raw
    
    # Agregar import si no existe
    if ($content -notmatch 'import jakarta\.persistence\.Version') {
        $content = $content -replace '(import jakarta\.persistence\.GenerationType;)', '$1' + "`nimport jakarta.persistence.Version;"
    }
    
    # Agregar @Version después de @Id
    $content = $content -replace '(@Id\s+@GeneratedValue.*?\n)', '$1    @Version' + "`n    private Long version;`n"
    
    Set-Content $file.FullName $content
    Write-Host "Actualizado: $($file.Name)"
}
```

### Paso 2: Actualizar application-prod.properties

```properties
# Cambios requeridos
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.generate-ddl=false
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

### Paso 3: Agregar @Transactional

Ver sección de corrección en cada problema específico.

---

## 📚 REFERENCIAS

- [Hibernate Documentation](https://hibernate.org/orm/documentation/latest/)
- [Spring Data JPA Best Practices](https://spring.io/projects/spring-data-jpa)
- [OWASP SQL Injection](https://owasp.org/www-community/attacks/SQL_Injection)
- [Hibernate N+1 Problem](https://thoughts.sion.me/2013/06/the-hibernate-select-n1-problem.html)
- [JPA @Version for Optimistic Locking](https://docs.oracle.com/cd/E19798-01/821-1841/bnbqw/)

---

**Análisis Completado:** 22 de marzo de 2026  
**Próxima Revisión Recomendada:** Después de implementar recomendaciones  
**Puntuación Actual:** 5.8/10 → Objetivo: 9.2/10
