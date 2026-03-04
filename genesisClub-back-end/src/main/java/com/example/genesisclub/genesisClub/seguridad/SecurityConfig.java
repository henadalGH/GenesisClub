package com.example.genesisclub.genesisClub.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTUtilityService jwtUtilityService;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(
            JWTUtilityService jwtUtilityService,
            UsuarioRepository usuarioRepository) {
        this.jwtUtilityService = jwtUtilityService;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return new JWTAuthorizationFilter(jwtUtilityService, usuarioRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. CORS: Debe configurarse explícitamente aquí
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 2. Desactivar CSRF para APIs REST
            .csrf(csrf -> csrf.disable())

            // 3. Manejo de sesión sin estado (JWT)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. Autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // Permitir todas las opciones (Preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Rutas públicas
                .requestMatchers(
                        "/api/auth/**",
                        "/api/usuario/registro",
                        "/api/solicitud/nuevo/**",
                        "/email/**",
                        "/api/invitacion/aceptar/**"
                ).permitAll()

                // Rutas de ADMIN
                .requestMatchers("/api/solicitud/pendientes/**").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/api/socio/**").hasRole("ADMIN")

                // Rutas para usuarios logueados
                .requestMatchers("/api/invitacion/**").authenticated()

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            // 5. Filtro JWT antes del de usuario/password
            .addFilterBefore(
                    jwtAuthorizationFilter(),
                    UsernamePasswordAuthenticationFilter.class
            )

            // 6. Manejo de errores de autenticación
            .exceptionHandling(ex ->
                    ex.authenticationEntryPoint((req, res, e) ->
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado")
                    )
            );

        return http.build();
    }

    // =========================================================
    // 🔥 CONFIGURACIÓN CORS INTEGRADA
    // =========================================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Orígenes permitidos
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://genesisclub-frontend.onrender.com"
        ));
        
        // Métodos permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Headers permitidos
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        
        // Headers expuestos (necesario para que el front vea el Token si lo mandas en el header)
        config.setExposedHeaders(List.of("Authorization"));
        
        // Permitir envío de credenciales/cookies
        config.setAllowCredentials(true);
        
        // Tiempo de caché para el preflight (opcional, ayuda al rendimiento)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}