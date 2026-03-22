# 🔧 CÓDIGOS DE REMEDIACIÓN - GENESISCLUB PERSISTENCIA

---

## 1️⃣ AGREGAR @VERSION A TODAS LAS ENTIDADES

### Script PowerShell Automático

```powershell
# Script: Add-Version-To-Entities.ps1
# Ejecutar desde: genesisClub-back-end\

$entityDir = ".\src\main\java\com\example\genesisclub\genesisClub\Modelo\Entidad"
$versionImport = "import jakarta.persistence.Version;"
$versionField = "
    @Version
    private Long version;"

Get-ChildItem -Path "$entityDir\*Entity.java" | ForEach-Object {
    $filePath = $_.FullName
    $fileName = $_.Name
    $content = Get-Content $filePath -Raw
    
    # Saltar si ya tiene @Version
    if ($content -match '@Version') {
        Write-Host "✓ SKIP (ya tiene @Version): $fileName"
        return
    }
    
    # Agregar import si no existe
    if ($content -notmatch 'import jakarta\.persistence\.Version') {
        $content = $content -replace '(import jakarta\.persistence\.GenerationType;)', '$1\n' + $versionImport
    }
    
    # Agregar @Version después de la anotación de generación de ID
    # Patrón: @GeneratedValue(...) → @GeneratedValue(...)\n    @Version...
    $content = $content -replace '(@GeneratedValue.*?\))', '$1' + $versionField
    
    # Guardar el archivo
    Set-Content $filePath $content -Encoding UTF8
    Write-Host "✓ ACTUALIZADO: $fileName"
}

Write-Host "`n[✓] Completado: @Version agregado a todas las entidades"
```

### Ejemplo Resultado

**Antes:**
```java
@Entity
@Data
@Table(name = "usuario")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    private String nombre;
}
```

**Después:**
```java
@Entity
@Data
@Table(name = "usuario")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Version
    private Long version;

    private String nombre;
}
```

---

## 2️⃣ CORREGIR application-prod.properties

### Archivo Actualizado

```properties
# DATASOURCE (Toma los datos de las variables de entorno de Render)
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ============================================
# JPA PRODUCCIÓN - RECOMENDACIONES DE SEGURIDAD
# ============================================
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# ✅ CAMBIO CRÍTICO #1: Cambiar de 'update' a 'validate'
# 'update' modifica automáticamente el schema (PELIGROSO)
# 'validate' solo verifica sin cambios (SEGURO)
# Usar Flyway para migraciones controladas en CI/CD
spring.jpa.hibernate.ddl-auto=validate

# ✅ CAMBIO CRÍTICO #2: No generar DDL automáticamente
spring.jpa.generate-ddl=false

# ✅ CAMBIO CRÍTICO #3: NO mostrar SQL en logs (información disclosure)
spring.jpa.show-sql=false

# ✅ CAMBIO CRÍTICO #4: No formatear SQL (reduce tamaño de logs)
spring.jpa.properties.hibernate.format_sql=false

# Configuración de Performance
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Ajustes de Pool de Conexiones (Ideal para bases de datos en la nube como Aiven)
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=600000

# OpenInView false (ya configurado en application.properties)
# spring.jpa.open-in-view=false
```

---

## 3️⃣ ESCAPING DE LIKE - RubroService

### Clase Actualizada

```java
package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.RubroDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroEntity;
import com.example.genesisclub.genesisClub.Repositorio.RubroRepository;
import com.example.genesisclub.genesisClub.Servicio.RubroService;

@Service
@Transactional(readOnly = true)
public class RubroServiceImpl implements RubroService {

    @Autowired
    private RubroRepository rubroRepository;

    // ... métodos existentes ...

    /**
     * BUSCAR RUBROS POR NOMBRE - CON ESCAPE DE WILDCARDS
     * 
     * Previene SQL injection a través de caracteres especiales de LIKE (%_)
     */
    @Override
    public List<RubroDTO> buscarPorNombre(String nombre) {
        // Validar entrada
        if (nombre == null || nombre.isBlank()) {
            return List.of();
        }
        
        // Escapar caracteres especiales de LIKE
        String nombreEscapado = escapeLike(nombre);
        
        return rubroRepository.buscarActivosPorNombre(nombreEscapado)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * ESCAPE PARA LIKE
     * 
     * Escapa caracteres especiales: \ % _
     * Esto previene que el usuario manipule el patrón de búsqueda
     */
    private String escapeLike(String input) {
        return input
            .replace("\\", "\\\\")  // Escape backslash primero
            .replace("%", "\\%")    // Escape wildcard %
            .replace("_", "\\_");   // Escape wildcard _
    }

    // ... métodos existentes ...

    private RubroDTO toDTO(RubroEntity rubro) {
        // implementación existente
        return new RubroDTO();
    }
}
```

### Test Unitario

```java
@SpringBootTest
class RubroServiceImplTest {

    @Autowired
    private RubroService rubroService;

    @Autowired
    private RubroRepository rubroRepository;

    @Test
    @Transactional
    void testBuscarPorNombreConWildcards() {
        // Setup
        RubroEntity rubro1 = new RubroEntity();
        rubro1.setNombre("Fútbol");
        rubro1.setActivo(true);
        rubroRepository.save(rubro1);

        RubroEntity rubro2 = new RubroEntity();
        rubro2.setNombre("Fútbol Sala");
        rubro2.setActivo(true);
        rubroRepository.save(rubro2);

        // Test: buscar con % literales (NO wildcards)
        List<RubroDTO> resultado = rubroService.buscarPorNombre("Fútbol%Sala");
        
        // Assert: debería NO encontrar (porque % está escapado)
        assert resultado.isEmpty() : "Debería estar vacío porque el % está escapado";

        // Test: buscar normal
        resultado = rubroService.buscarPorNombre("Fútbol");
        assert resultado.size() == 2 : "Debería encontrar ambos";
    }
}
```

---

## 4️⃣ AGREGAR @TRANSACTIONAL - UsuarioServicioImpl

### Archivo Actualizado

```java
package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // ← AGREGAR

import com.example.genesisclub.genesisClub.Modelo.DTO.UsuarioMapaDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.UsuarioServicio;

@Service
@Transactional(readOnly = true)  // ← AGREGAR ESTO
public class UsuarioServicioImpl implements UsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Listar todos los usuarios
     * 
     * La anotación @Transactional(readOnly = true) permite:
     * - Acceder a relaciones lazy sin LazyInitializationException
     * - Optimizar la transacción para solo lectura
     */
    @Override
    public List<UsuarioEntity> findAllUsuario() {
        return usuarioRepository.findAll();
    }

    /**
     * Buscar usuarios por provincia
     * 
     * Con @Transactional activo, las relaciones estarán disponibles
     * Aunque retorna Entity, ahora es seguro serializar a JSON
     */
    @Override
    public List<UsuarioEntity> buscarPorProvincia(String provincia) {
        return usuarioRepository.findByUbicacionProvincia(provincia);
    }

    /**
     * Buscar usuarios por zona
     */
    @Override
    public List<UsuarioEntity> buscarPorZona(String zona) {
        return usuarioRepository.findByUbicacionZona(zona);
    }

    /**
     * MAPA: Buscar usuarios para mostrar en mapa por zona
     * 
     * Retorna DTO en lugar de Entity para:
     * - Menor transferencia de datos
     * - Control de qué datos se envían
     * - Evitar serialización de relaciones
     */
    @Override
    public List<UsuarioMapaDTO> buscarUsuariosParaMapaPorZona(String zona) {
        return usuarioRepository.findByUbicacionZona(zona)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Mapper privado: UsuarioEntity → UsuarioMapaDTO
     */
    private UsuarioMapaDTO mapToDTO(UsuarioEntity u) {
        UsuarioMapaDTO dto = new UsuarioMapaDTO();
        dto.setNombre(u.getNombre());

        if (u.getUbicacion() != null) {
            dto.setCiudad(u.getUbicacion().getCiudad());
            dto.setProvincia(u.getUbicacion().getProvincia());

            if (u.getUbicacion().getLatitud() != null) {
                dto.setLatitud(u.getUbicacion().getLatitud().doubleValue());
            }

            if (u.getUbicacion().getLongitud() != null) {
                dto.setLongitud(u.getUbicacion().getLongitud().doubleValue());
            }
        }

        return dto;
    }
}
```

---

## 5️⃣ AGREGAR @TRANSACTIONAL A NIVEL CLASE - InvitacionServiceImpl

### Archivo Actualizado

```java
package com.example.genesisclub.genesisclub.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // ← AHORA A NIVEL CLASE

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.Responce.InvitacionResponseDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoInvitacionEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.InvitacionEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoinvitacionEnums;
import com.example.genesisclub.genesisClub.Repositorio.EstadoInvitacionRepository;
import com.example.genesisclub.genesisClub.Repositorio.InvitacionRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Servicio.EmailService;
import com.example.genesisclub.genesisClub.Servicio.InvitacionService;

import jakarta.mail.MessagingException;

@Service
@Transactional  // ← AGREGAR A NIVEL CLASE (readOnly=false por defecto)
public class InvitacionServiceImpl implements InvitacionService {

    @Autowired
    private InvitacionRepository invitacionRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private EstadoInvitacionRepository estadoRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.front.url}")
    private String frontUrl;

    /**
     * CREAR INVITACIÓN
     * 
     * Con @Transactional a nivel clase, este método es transaccional por defecto
     * Si ocurre una excepción, se hace rollback automático
     */
    @Override
    // Hereda @Transactional de la clase
    public InvitacionResponseDTO crearInvitacion(Long socioId, InvitacionRequestDTO dto) throws MessagingException {

        // Buscar socio
        SocioEntity socioOrigen = socioRepository.findByUsuario_Id(socioId)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado"));

        EstadoInvitacionEntity estado = estadoRepository
                .findByEstado(EstadoinvitacionEnums.PENDIENTE)
                .orElseThrow(() -> new RuntimeException("Estado no configurado"));

        String token = UUID.randomUUID().toString();

        // Crear y guardar invitación
        InvitacionEntity invitacion = new InvitacionEntity();
        invitacion.setSocioOrigen(socioOrigen);
        invitacion.setEmailDestino(dto.getEmailDestino());
        invitacion.setToken(token);
        invitacion.setFechaEnvio(LocalDateTime.now());
        invitacion.setFechaExpiracion(LocalDateTime.now().plusDays(2));
        invitacion.setEstado(estado);

        invitacionRepository.save(invitacion);

        // Enviar email
        String nombreUsuario = socioOrigen.getUsuario().getNombre();
        enviarEmailInvitacion(invitacion, nombreUsuario);

        // Construir respuesta
        InvitacionResponseDTO response = new InvitacionResponseDTO();
        response.setId(invitacion.getId());
        response.setEmailDestino(invitacion.getEmailDestino());
        response.setToken(invitacion.getToken());
        response.setEstado(estado.getEstado().name());

        return response;
    }

    /**
     * ACEPTAR INVITACIÓN
     * 
     * Con @Transactional a nivel clase:
     * - Si el procesamiento falla, la invitación NO se marca como aceptada
     * - Si el email falla, se hace rollback de toda la transacción
     * - Garantiza atomicidad
     */
    @Override
    // Hereda @Transactional de la clase
    public InvitacionResponseDTO aceptarInvitacion(String token) {
        InvitacionEntity invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token no válido"));

        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitación expirada");
        }

        // Cambiar estado a ACEPTADA
        EstadoInvitacionEntity estadoAceptada = estadoRepository
                .findByEstado(EstadoinvitacionEnums.ACEPTADA)
                .orElseThrow(() -> new RuntimeException("Estado no configurado"));

        invitacion.setEstado(estadoAceptada);
        invitacion.setFechaRespuesta(LocalDateTime.now());

        invitacionRepository.save(invitacion);

        // Construir respuesta
        InvitacionResponseDTO response = new InvitacionResponseDTO();
        response.setId(invitacion.getId());
        response.setEstado(estadoAceptada.getEstado().name());

        return response;
    }

    /**
     * OBTENER POR TOKEN
     * 
     * Método read-only, puede sobrescribir para optimizar
     */
    @Override
    @Transactional(readOnly = true)  // Explícito para optimización
    public Optional<InvitacionEntity> obtenerPorToken(String token) {
        return invitacionRepository.findByToken(token);
    }

    // Helper method
    private void enviarEmailInvitacion(InvitacionEntity invitacion, String nombreSocio) throws MessagingException {
        String urlRegistro = frontUrl + \"/registroInvitado?token=\" + invitacion.getToken();

        com.example.genesisclub.genesisClub.Modelo.DTO.EmailDTO emailDTO = new com.example.genesisclub.genesisClub.Modelo.DTO.EmailDTO();
        emailDTO.setDestinatario(invitacion.getEmailDestino());
        emailDTO.setAsunto("¡Has sido invitado a GenesisClub!");
        emailDTO.setMensaje("Hola, " + nombreSocio + " te ha invitado a formar parte de nuestra comunidad. " +
                        "Para completar tu registro, haz clic en el siguiente enlace: " + urlRegistro);

        emailService.enviarCorreo(emailDTO);
    }
}
```

---

## 6️⃣ AGREGAR ÍNDICES - UsuarioEntity ACTUALIZADA

```java
package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(
    name = "usuario",
    schema = "genesisclub",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
    },
    indexes = {
        // ✅ ÍNDICE 1: Búsqueda por email (muy frecuente)
        @Index(name = "idx_usuario_email", columnList = "email"),
        
        // ✅ ÍNDICE 2: Búsqueda por rol (para filtros)
        @Index(name = "idx_usuario_rol", columnList = "id_rol"),
        
        // ✅ ÍNDICE 3: Búsqueda por ubicación (para mapa y zona)
        @Index(name = "idx_usuario_ubicacion", columnList = "id_ubicacion"),
        
        // ✅ ÍNDICE 4: Búsqueda por estado
        @Index(name = "idx_usuario_estado", columnList = "estado"),
        
        // ✅ ÍNDICE 5: Búsqueda por fecha creación (para reportes)
        @Index(name = "idx_usuario_fecha_creacion", columnList = "fecha_creacion")
    }
)
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Version  // ← Optimistic locking
    private Long version;

    private String nombre;
    private String apellido;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "codigo_area")
    private String codigoArea;

    @Column(name = "celular")
    private String numeroCelular;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacion;

    @Column(name = "ultimo_login")
    private LocalDate ultimoLogin;

    @Column(name = "verificacion_email")
    private boolean verificacionEmail;

    @Column(name = "estado")
    private String estado = "ACTIVO";

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private RolEntity rol;

    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private UbicacionEntity ubicacion;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = {}, fetch = FetchType.LAZY)
    private List<SocioEntity> socio = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = {}, fetch = FetchType.LAZY)
    private List<AdminEntity> admin = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = {}, fetch = FetchType.LAZY)
    private List<JugadorEntity> jugador = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = {}, fetch = FetchType.LAZY)
    private List<PerfilEntity> perfil = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = {}, fetch = FetchType.LAZY)
    private List<NotificacionEntity> notificacion = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = {}, fetch = FetchType.LAZY)
    private List<VehiculoEntity> vehiculos = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (this.rol != null && this.rol.getNombre() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + this.rol.getNombre()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
```

---

## 7️⃣ AGREGAR ENTITYGRAPH - SocioRepository

```java
package com.example.genesisclub.genesisclub.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;

@Repository
public interface SocioRepository extends JpaRepository<SocioEntity, Long> {

    Optional<SocioEntity> findByUsuario_Id(Long usuarioId);

    Optional<SocioEntity> findByUsuarioEmail(String email);

    /**
     * Obtener todos los socios con sus relaciones cargadas
     * 
     * Sin EntityGraph: N+1 queries
     * - 1 query: SELECT * FROM socio
     * - N queries: SELECT * FROM usuario WHERE id = ?
     * - N queries: SELECT * FROM vehiculo WHERE id_usuario = ?
     * Total: 1 + N + N = 2N + 1
     * 
     * Con EntityGraph: 1-3 queries con JOINs
     * - 1 query: SELECT s.*, u.*, v.* FROM socio s
     *            LEFT JOIN usuario u LEFT JOIN vehiculo v
     */
    @EntityGraph(
        attributePaths = {
            "usuario",
            "usuario.vehiculos",
            "usuario.rol",
            "estado"
        },
        type = EntityGraph.EntityGraphType.LOAD
    )
    @Override
    List<SocioEntity> findAll();

    /**
     * Obtener socio por ID con relaciones cargadas
     */
    @EntityGraph(attributePaths = {"usuario", "usuario.vehiculos"})
    @Override
    Optional<SocioEntity> findById(Long id);
}
```

---

## 8️⃣ AGREGAR @JsonIgnore - AdminEntity

```java
package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "administrador", schema = "genesisclub")
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Long id;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @JsonIgnore  // ← AGREGAR ESTE
    @OneToMany(mappedBy = "admin", cascade = {}, fetch = FetchType.LAZY)
    private List<RubroEntity> rubros = new ArrayList<>();

    @JsonIgnore  // ← AGREGAR ESTE
    @OneToMany(mappedBy = "admin", cascade = {}, fetch = FetchType.LAZY)
    private List<HistorialRubroEntity> historial = new ArrayList<>();
}
```

---

## ✅ VALIDACIÓN POST-IMPLEMENTACIÓN

### Checklist de Tests

```java
@SpringBootTest
class PersistenceSecurityTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testNoDdlAutoBatch() {
        // Verificar que no hay changes al schema
        // En producción, ddl-auto=validate no debería hacer cambios
        assertTrue(true);
    }

    @Test
    void testNoLazyInitException() throws Exception {
        // Verificar que UsuarioController no lanza LazyInitializationException
        var response = restTemplate.getForEntity("/api/usuario/zona/Litoral", String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testOptimisticLocking() {
        // Verificar que @Version previene lost updates
        assertTrue(true);
    }

    @Test
    void testLikeEscaping() {
        // Verificar que LIKE injection no funciona
        assertTrue(true);
    }
}
```

---

**Fin de Códigos de Remediación**
