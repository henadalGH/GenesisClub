# 🔍 ANÁLISIS EXHAUSTIVO: SEGURIDAD JWT Y AUTENTICACIÓN
**Proyecto:** GenesisClub Backend  
**Fecha:** 22 de Marzo de 2026  
**Nivel:** Completo

---

## 📊 RESUMEN EJECUTIVO

| Crítica | Alta | Media | Baja | Total |
|---------|------|-------|------|-------|
| 2 | 4 | 6 | 3 | 15 |

---

## 🔴 PROBLEMAS CRÍTICOS (SEVERIDAD: CRÍTICA)

### 1. ❌ [CRÍTICA] Contraseña sin encriptación en solicitudes aceptadas
- **Archivo:** [RegistroUsuarioServicioImpl.java](RegistroUsuarioServicioImpl.java#L96)
- **Línea:** 96
- **Problema:**
```java
if (desdeSolicitud) {
    usuario.setPassword(dto.getPassword());  // ❌ TEXTO PLANO
} else {
    usuario.setPassword(encoder.encode(dto.getPassword()));  // ✓ Encriptado
}
```
- **Impacto:** Las contraseñas de usuarios registrados desde solicitudes (SOCIO, JUGADOR) se guardan en **TEXTO PLANO** en la base de datos
- **Flujo afectado:** 
  - Solicitudes de Socio → aceptadas → contraseña sin encriptar
  - Solicitudes de Jugador → aceptadas → contraseña sin encriptar
- **Recomendación:** 
```java
// CORRECCIÓN:
usuario.setPassword(encoder.encode(dto.getPassword()));  // SIEMPRE encriptar
```

---

### 2. ❌ [CRÍTICA] Blacklist de logout NO se valida en filtro
- **Archivo:** [JWTAuthorizationFilter.java](JWTAuthorizationFilter.java#L45)
- **Línea:** 44-46
- **Problema:**
```java
// En AuthServiceImpl.java (líneas 134-142):
private final Map<String, String> revokedTokens = new HashMap<>();

public void logout(String token) {
    revokedTokens.put(token, "revoked");
}

public boolean isRevoked(String token) {
    return revokedTokens.containsKey(token);
}

// ❌ NUNCA se verifica en JWTAuthorizationFilter
if (!jwtUtilityService.isTokenValid(token)) {  // Solo valida firma y expiración
    filterChain.doFilter(request, response);
    return;
}
```
- **Impacto:** Un usuario que hace logout puede seguir usando su token JWT porque:
  - La blacklist se almacena en memoria (desaparece al reiniciar)
  - El método `isRevoked()` NO se llama en el filtro
  - El filtro solo valida firma y expiración
- **Escenario de ataque:**
  1. Usuario A hace logout → token agregado a blacklist
  2. Si el servidor reinicia → blacklist se limpia
  3. Usuario A sigue con el mismo token y accede
- **Recomendación:**
```java
// En JWTAuthorizationFilter.java, línea 44:
if (!jwtUtilityService.isTokenValid(token)) {
    filterChain.doFilter(request, response);
    return;
}

// AGREGAR esta validación:
if (authService.isRevoked(token)) {  // Verificar blacklist
    log.warn("Token revocado detectado");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}
```
- **Nota:** La blacklist en memoria es provisional. Se recomienda usar Redis o BD para persistencia.

---

## 🟠 PROBLEMAS ALTOS (SEVERIDAD: ALTA)

### 3. ❌ [ALTA] No valida audience (aud) claim en JWT
- **Archivo:** [JWTUtilityServiceImpl.java](JWTUtilityServiceImpl.java#L40-65)
- **Línea:** 40-65 (generateJWT), 100 (parseJWT)
- **Problema:**
```java
JWTClaimsSet claimsSet = claimsBuilder  // No incluye "aud" claim
    .subject(userId.toString())
    .claim("rol", rol)
    .issueTime(now)
    .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
    .build();

// En parseJWT() NO se valida aud
if (claims.getExpirationTime().before(new Date())) {
    throw new JOSEException("Token expirado");
}
// ❌ Falta: validar audience
```
- **Impacto:** Si un atacante captura un JWT, podría reutilizarlo en otros servicios/aplicaciones que acepten JWT firmados con la misma clave privada
- **RFC 7519:** Requiere validación de `aud` claim
- **Recomendación:**
```java
// En generateJWT():
JWTClaimsSet claimsSet = claimsBuilder
    .subject(userId.toString())
    .claim("rol", rol)
    .audience("genesisclub-api")  // ✓ AGREGAR
    .issueTime(now)
    .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
    .build();

// En parseJWT():
JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

// ✓ AGREGAR validación:
if (claims.getAudience() == null || !claims.getAudience().contains("genesisclub-api")) {
    throw new JOSEException("Audience inválida");
}

if (claims.getExpirationTime().before(new Date())) {
    throw new JOSEException("Token expirado");
}
```

---

### 4. ❌ [ALTA] Token JWT con expiración muy larga (4 horas)
- **Archivo:** [JWTUtilityServiceImpl.java](JWTUtilityServiceImpl.java#L33)
- **Línea:** 33
- **Problema:**
```java
private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 4;  // ❌ 4 horas
```
- **Impacto:**
  - Una contraseña comprometida permite acceso durante 4 horas
  - Ventana de ataque muy amplia
  - No hay opción de refresh token para sesiones cortas
- **Estándar recomendado:** 15-30 minutos para access token
- **Recomendación:**
```java
// Configurar en properties:
// application.properties:
jwt.expiration-time=900000  # 15 minutos (en ms)

// Usar en la clase:
@Value("${jwt.expiration-time:900000}")
private static long EXPIRATION_TIME;
```
- **Agregar refresh token:**
  - Access token: 15 minutos
  - Refresh token: 7 días (en tabla especial, revocable)

---

### 5. ⚠️ [ALTA] Sin validación de JTI (JWT ID) - No hay control de token reuse
- **Archivo:** [JWTUtilityServiceImpl.java](JWTUtilityServiceImpl.java#L40-65)
- **Línea:** 40-65
- **Problema:**
  - No hay `jti` claim (JWT ID único)
  - Imposible revocar tokens específicos sin impactar toda la sesión
  - No hay forma de detectar reutilización de tokens
- **Impacto:** 
  - Si se roba un token, no hay forma de identificarlo de manera única
  - Solo se puede revocar por email/usuario
- **Recomendación:**
```java
// En generateJWT():
import java.util.UUID;

JWTClaimsSet claimsSet = claimsBuilder
    .subject(userId.toString())
    .claim("rol", rol)
    .jwtID(UUID.randomUUID().toString())  // ✓ AGREGAR ID único
    .audience("genesisclub-api")
    .issueTime(now)
    .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
    .build();
```
- **Entonces en logout:**
  - Guardar revoked JTI en BD (no en memoria)
  - Validar JTI en cada request

---

### 6. ❌ [ALTA] Blacklist de logout en memoria - Se pierde al reiniciar
- **Archivo:** [AuthServiceImpl.java](AuthServiceImpl.java#L134-142)
- **Línea:** 134-142
- **Problema:**
```java
private final Map<String, String> revokedTokens = new HashMap<>();  // RAM, no persistente

public void logout(String token) {
    revokedTokens.put(token, "revoked");  // Desaparece al restart
}
```
- **Impacto:**
  - Restart del servidor → blacklist limpia
  - Tokens "revocados" vuelven a ser válidos
  - Múltiples instancias (replicas) no comparten blacklist
- **Recomendación:** Usar Redis o base de datos
```java
// Usar Redis (mejor):
@Autowired
private RedisTemplate<String, String> redisTemplate;

public void logout(String token) {
    redisTemplate.opsForValue().set("revoked:" + token, "true", 
        Duration.ofHours(4));  // Expires con mismo TTL que token
}

public boolean isRevoked(String token) {
    return redisTemplate.hasKey("revoked:" + token);
}
```

---

## 🟡 PROBLEMAS MEDIOS (SEVERIDAD: MEDIA)

### 7. ⚠️ [MEDIA] Manejo genérico de excepciones en filtro JWT
- **Archivo:** [JWTAuthorizationFilter.java](JWTAuthorizationFilter.java#L62-68)
- **Línea:** 62-68
- **Problema:**
```java
try {
    String token = header.substring(7);
    if (!jwtUtilityService.isTokenValid(token)) {
        filterChain.doFilter(request, response);  // ❌ Pasa sin error
        return;
    }
    // ...
} catch (Exception e) {
    log.error("Error al procesar token JWT", e);  // ❌ Log genérico
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}
```
- **Impacto:**
  - Si token inválido → pasa al siguiente filtro (sin error)
  - Las excepciones se capturan pero no se comunican al cliente
  - Difícil de debuggear en producción
- **Recomendación:**
```java
try {
    String token = header.substring(7);
    
    if (!jwtUtilityService.isTokenValid(token)) {
        log.warn("Token inválido: {}", token.substring(0, 20) + "...");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\":\"Token inválido o expirado\"}");
        return;
    }
    
    // ... resto del código
} catch (JWTException e) {
    log.error("Error de validación JWT: {}", e.getMessage());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
} catch (Exception e) {
    log.error("Error inesperado en filtro JWT", e);
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}
```

---

### 8. ⚠️ [MEDIA] Sin issuer (iss) claim - No hay validación de donde vino el token
- **Archivo:** [JWTUtilityServiceImpl.java](JWTUtilityServiceImpl.java#L40-50)
- **Línea:** 40-50
- **Problema:**
```java
JWTClaimsSet claimsSet = claimsBuilder
    .subject(userId.toString())
    .claim("rol", rol)
    // ❌ Falta: .issuer("genesisclub-auth")
    .issueTime(now)
    .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
    .build();
```
- **Impacto:** Un token de otro servicio/atacante podría ser aceptado si usa la misma clave
- **Recomendación:**
```java
// En generateJWT():
JWTClaimsSet claimsSet = claimsBuilder
    .subject(userId.toString())
    .claim("rol", rol)
    .issuer("genesisclub-auth")  // ✓ AGREGAR
    .audience("genesisclub-api")
    .jwtID(UUID.randomUUID().toString())
    .issueTime(now)
    .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
    .build();

// En parseJWT():
if (!"genesisclub-auth".equals(claims.getIssuer())) {
    throw new JOSEException("Issuer inválido");
}
```

---

### 9. ⚠️ [MEDIA] Sin rate limiting en endpoint de login
- **Archivo:** [LoginController.java](LoginController.java#L24-37)
- **Línea:** 24-37, [AuthServiceImpl.java](AuthServiceImpl.java#L42-87)
- **Problema:**
```java
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(
        @RequestBody LoginDTO loginRequest
) throws Exception {
    // ❌ Sin rate limiting, sin delay, sin contador de intentos
```
- **Impacto:** Vulnerable a ataques de fuerza bruta
- **Recomendación:**
```java
// Agregar en SecurityConfig:
@Bean
public RateLimitingInterceptor rateLimitingInterceptor() {
    return RateLimitingInterceptor.builder()
        .requestLimit(5)  // 5 intentos
        .windowSize(Duration.ofMinutes(15))
        .build();
}

// O usar annotation:
@PostMapping("/login")
@RateLimit(limit = 5, timeWindow = 15)  // 5 intentos cada 15 min
public ResponseEntity<Map<String, Object>> login(...) {
```

---

### 10. ⚠️ [MEDIA] Mensaje de error diferencia email vs contraseña
- **Archivo:** [AuthServiceImpl.java](AuthServiceImpl.java#L48-51)
- **Línea:** 48-51
- **Problema:**
```java
UsuarioEntity usuario = usuarioRepository
        .findByEmail(login.getEmail())
        .orElse(null);

if (usuario == null || !encoder.matches(login.getPassword(), usuario.getPassword())) {
    response.put("success", false);
    response.put("message", ERROR_MESSAGE);  // ✓ MISMO mensaje (BIEN)
    return response;
}
```
- **Nota:** Este está BIEN implementado. ✓ Mantener así.

---

### 11. ⚠️ [MEDIA] Sin notificación de logout - Usuario no se entera
- **Archivo:** [LoginController.java](LoginController.java#L60-75)
- **Línea:** 60-75
- **Problema:**
```java
@PostMapping("/logout")
public ResponseEntity<String> logout(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.badRequest().body("Token no proporcionado");
    }
    String token = authHeader.substring(7);
    authService.logout(token);
    return ResponseEntity.ok("Sesión cerrada con éxito");
    // ❌ No hay:
    // - Notificación al usuario
    // - Análisis de intentos fallidos de acceso
    // - Log de auditoría
}
```
- **Impacto:** No hay modo de alertar al usuario si su sesión fue cerrada remotamente
- **Recomendación:**
```java
public ResponseEntity<String> logout(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.badRequest().body("Token no proporcionado");
    }
    
    String token = authHeader.substring(7);
    Long userId = jwtUtilityService.getUserId(token);
    
    authService.logout(token);
    
    // ✓ AGREGAR:
    auditService.logLogout(userId, LocalDateTime.now());
    
    // Notificar al usuario (opcional, pero recomendado)
    // emailService.sendLogoutNotification(userId);
    
    return ResponseEntity.ok("Sesión cerrada");
}
```

---

### 12. ⚠️ [MEDIA] Sin NotBefore claim (nbf) - Validación de tiempo de inicio
- **Archivo:** [JWTUtilityServiceImpl.java](JWTUtilityServiceImpl.java#L40-65)
- **Línea:** 40-65
- **Problema:**
```java
JWTClaimsSet claimsSet = claimsBuilder
    .subject(userId.toString())
    .claim("rol", rol)
    // ❌ Falta: .notBeforeTime(now)
    .issueTime(now)
    .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
    .build();
```
- **Impacto:** Bajo en este caso, pero es estándar RFC 7519
- **Recomendación:**
```java
Date now = new Date();
JWTClaimsSet claimsSet = claimsBuilder
    .subject(userId.toString())
    .claim("rol", rol)
    .notBeforeTime(now)  // ✓ AGREGAR
    .issueTime(now)
    .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
    .build();
```

---

## 🟢 PROBLEMAS BAJOS (SEVERIDAD: BAJA)

### 13. ℹ️ [BAJA] Contraseña fuerte en RegistroDTO pero no en SolicitudDTO
- **Archivo:** [RegistroDTO.java](RegistroDTO.java#L39-45)
- **Línea:** 39-45
- **¿Qué está bien?:**
```java
@Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
    message = "La contraseña debe tener mínimo 8 caracteres, mayúscula, minúscula, número y símbolo"
)
private String password;  // ✓ EXCELENTE
```
- **Problema:** No verificamos si SolicitudDTO y SolicitudJugadorDTO tienen las mismas validaciones
- **Recomendación:** Verificar que TODAS las solicitudes validen contraseña fuerte

---

### 14. ℹ️ [BAJA] Sin validación de "NotOnOrAfter" en renovación
- **Archivo:** [AuthServiceImpl.java](AuthServiceImpl.java#L39-130)
- **Nota:** No hay endpoint de refresh token
- **Recomendación:** Implementar refresh token pattern:
```java
@PostMapping("/refresh")
public ResponseEntity<?> refresh(@RequestBody RefreshTokenDTO dto) {
    if (jwtUtilityService.isRefreshTokenValid(dto.getRefreshToken())) {
        String newAccessToken = jwtUtilityService.generateJWT(...);
        return ResponseEntity.ok(Map.of("token", newAccessToken));
    }
    return ResponseEntity.unauthorized().build();
}
```

---

### 15. ℹ️ [BAJA] Sin verificación de IP en tokens
- **Archivo:** [JWTAuthorizationFilter.java](JWTAuthorizationFilter.java#L20-70)
- **Nota:** Token no incluye IP ni User-Agent
- **Recomendación (opcional):** Agregar claim de IP para detectar cambios:
```java
String clientIp = request.getHeader("X-Forwarded-For") != null 
    ? request.getHeader("X-Forwarded-For") 
    : request.getRemoteAddr();

// Agregar a claims
claimsBuilder.claim("ip", clientIp);

// Validar en filtro
String tokenIp = claims.getStringClaim("ip");
String currentIp = obtenerClientIp(request);
if (!tokenIp.equals(currentIp)) {
    log.warn("IP diferente: token={}, actual={}", tokenIp, currentIp);
    // Decidir si rechazar o solo alertar
}
```

---

## 📋 CONFIGURACIONES CORRECTAS (SIN PROBLEMAS)

### ✅ BCryptPasswordEncoder
- **Archivo:** [SecurityConfig.java](SecurityConfig.java#L72-74), [AuthServiceImpl.java](AuthServiceImpl.java#L37)
- **Estado:** ✓ Correcto
- Usa BCrypt con salt automático
- No hay secretos hardcodeados

### ✅ RS256 (RSA Signature)
- **Archivo:** [JWTUtilityServiceImpl.java](JWTUtilityServiceImpl.java#L60)
- **Estado:** ✓ Correcto
- RSA es más seguro que HS256
- Usa par de claves pública/privada

### ✅ SessionCreationPolicy.STATELESS
- **Archivo:** [SecurityConfig.java](SecurityConfig.java#L47)
- **Estado:** ✓ Correcto
- No hay sesiones servidor (stateless)

### ✅ CORS configurado
- **Archivo:** [SecurityConfig.java](SecurityConfig.java#L64-76)
- **Estado:** ✓ Correcto
- Acepta localhost:4200 y render.com
- Expone header Authorization

### ✅ CSRF deshabilitado para JWT
- **Archivo:** [SecurityConfig.java](SecurityConfig.java#L46)
- **Estado:** ✓ Correcto
- JWT no necesita CSRF token

---

## 🛠️ PLAN DE CORRECCIÓN RECOMENDADO

### Fase 1: CRÍTICA (INMEDIATO)
1. **Encriptar contraseñas en solicitudes** [problema #1]
   - Modificar RegistroUsuarioServicioImpl.java línea 96
   - Re-encriptar contraseñas existentes sin encriptar

2. **Validar revocación en filtro** [problema #2]
   - Agregar `isRevoked()` check en JWTAuthorizationFilter
   - O mejor: migrar a Redis/BD

### Fase 2: ALTA (URGENTE - Esta semana)
3. **Agregar audience y issuer claims** [problemas #3, #8]
4. **Reducir expiración a 15 min + refresh token** [problema #4]
5. **Agregar JTI claim** [problema #5]
6. **Migrar blacklist a Redis** [problema #6]

### Fase 3: MEDIA (Esta quincena)
7. **Mejorar manejo de excepciones** [problema #7]
8. **Agregar rate limiting** [problema #9]
9. **Audit logging de logout** [problema #11]
10. **Agregar nbf claim** [problema #12]

---

## 🔐 CÓDIGO DE EJEMPLO (Spring Security + JWT SEGURO)

```java
// Archivo: JWTUtilityServiceImpl.java (MEJORADO)
@Service
public class JWTUtilityServiceImpl implements JWTUtilityService {

    @Value("${jwt.expiration-time:900000}")  // 15 min
    private long EXPIRATION_TIME;
    
    @Value("${jwt.issuer:genesisclub-auth}")
    private String JWT_ISSUER;
    
    @Value("${jwt.audience:genesisclub-api}")
    private String JWT_AUDIENCE;

    @Override
    public String generateJWT(Long userId, String rol, Long socioId, Long jugadorId, Long adminId) throws Exception {
        
        PrivateKey privateKey = loadPrivateKey(privateResource);
        JWSSigner signer = new RSASSASigner(privateKey);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userId.toString())
            .issuer(JWT_ISSUER)                              // ✓
            .audience(JWT_AUDIENCE)                          // ✓
            .claim("rol", rol)
            .jwtID(UUID.randomUUID().toString())             // ✓
            .claim("socioId", socioId)
            .claim("jugadorId", jugadorId)
            .claim("adminId", adminId)
            .issueTime(now)
            .notBeforeTime(now)                              // ✓
            .expirationTime(expiration)
            .build();
        
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.RS256),
            claimsSet
        );
        
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @Override
    public JWTClaimsSet parseJWT(String jwt) throws Exception {
        
        PublicKey publicKey = loadPublicKey(publicResource);
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        
        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Firma inválida");
        }
        
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        
        // ✓ Validaciones adicionales:
        if (!JWT_ISSUER.equals(claims.getIssuer())) {
            throw new JOSEException("Issuer inválido");
        }
        
        if (claims.getAudience() == null || !claims.getAudience().contains(JWT_AUDIENCE)) {
            throw new JOSEException("Audience inválida");
        }
        
        if (claims.getExpirationTime().before(now)) {
            throw new JOSEException("Token expirado");
        }
        
        return claims;
    }
}
```

---

## 📌 CHECKLIST DE AUDITORÍA

- [ ] Contraseñas en solicitudes encriptadas
- [ ] Blacklist validada en filtro
- [ ] Audience claim presente y validado
- [ ] Issuer claim presente y validado
- [ ] JTI claim presente (para revocación individual)
- [ ] NBF claim presente
- [ ] Expiración reducida a 15 min
- [ ] Refresh token implementado
- [ ] Rate limiting en /login
- [ ] Blacklist en Redis o BD (no memoria)
- [ ] Audit logging implementado
- [ ] Manejo de excepciones mejorado
- [ ] Protección contra token reuse
- [ ] IP verificada (opcional)

---

## 📚 REFERENCIAS

- RFC 7519: JSON Web Token (JWT) - https://tools.ietf.org/html/rfc7519
- OWASP JWT Cheat Sheet - https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html
- Spring Security JWT - https://spring.io/blog/2015/01/12/spring-and-security-updates
- CWE-384: Session Fixation - https://cwe.mitre.org/data/definitions/384.html

---

**Generado:** 22 de Marzo de 2026
**Clasificación:** CONFIDENCIAL
