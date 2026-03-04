package com.example.genesisclub.genesisClub.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private JWTUtilityService jwtUtilityService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return new JWTAuthorizationFilter(jwtUtilityService, usuarioRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // 1. CORS: Usa la configuración de tu archivo CrossConfig
            .cors(Customizer.withDefaults()) 
            
            // 2. CSRF: Deshabilitado para APIs REST (Stateless)
            .csrf(csrf -> csrf.disable())

            // 3. GESTIÓN DE PETICIONES (Rutas)
            .authorizeHttpRequests(auth -> auth
                // Permitir siempre las peticiones de verificación de CORS (OPTIONS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // --- RUTAS PÚBLICAS ---
                .requestMatchers(
                    "/api/auth/**", 
                    "/api/usuario/registro", 
                    "/api/solicitud/nuevo/**", // Agregamos /** para ser más tolerantes en Render
                    "/email/**", 
                    "/api/invitacion/aceptar/**"
                ).permitAll()

                // --- RUTAS DE ADMINISTRADOR ---
                .requestMatchers("/api/solicitud/pendientes/**").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/api/socio/**").hasRole("ADMIN")

                // --- RUTAS AUTENTICADAS ---
                .requestMatchers("/api/invitacion/**").authenticated()

                // --- EL RESTO REQUIERE LOGIN ---
                .anyRequest().authenticated()
            )

            // 4. POLÍTICA SIN ESTADO (No guardamos sesiones en el servidor)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 5. FILTRO JWT: Se ejecuta antes del filtro de usuario/password
            .addFilterBefore(
                jwtAuthorizationFilter(), 
                UsernamePasswordAuthenticationFilter.class
            )

            // 6. MANEJO DE ERRORES: Personalizamos el 401
            .exceptionHandling(ex -> 
                ex.authenticationEntryPoint((req, res, authEx) -> {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado - Token inválido o ruta protegida");
                })
            )

            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}