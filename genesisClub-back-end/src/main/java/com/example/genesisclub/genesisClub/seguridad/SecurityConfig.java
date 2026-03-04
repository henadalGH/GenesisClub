package com.example.genesisclub.genesisClub.seguridad;

import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTUtilityService jwtUtilityService;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(JWTUtilityService jwtUtilityService, UsuarioRepository usuarioRepository) {
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
            // 1. CORS: Configuración explícita para producción
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. CSRF: Deshabilitado para APIs REST
            .csrf(csrf -> csrf.disable())

            // 3. Estado: Sin sesión (JWT puro)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. RUTAS: Configuración de permisos
            .authorizeHttpRequests(auth -> auth
                // Permitir siempre el Preflight (OPTIONS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // RUTAS PÚBLICAS (Exactas, sin asteriscos conflictivos)
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/registro").permitAll()
                .requestMatchers("/api/usuario/registro").permitAll()
                .requestMatchers("/api/solicitud/nuevo").permitAll()
                .requestMatchers("/api/solicitud/nuevo/").permitAll()
                .requestMatchers("/email/enviar").permitAll()
                
                // RUTAS DE ADMIN (Asegúrate de que el usuario tenga ROLE_ADMIN en DB)
                .requestMatchers("/api/solicitud/pendientes").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/actualizar").hasRole("ADMIN")
                .requestMatchers("/api/socio/**").hasRole("ADMIN")

                // Todo lo demás requiere estar logueado
                .anyRequest().authenticated()
            )

            // 5. FILTROS: JWT antes que el de usuario/password
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)

            // 6. MANEJO DE ERRORES: Si algo falla, devuelve 401 limpio
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
                })
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Orígenes permitidos (Local + Render)
        config.setAllowedOrigins(List.of(
            "http://localhost:4200", 
            "https://genesisclub-frontend.onrender.com"
        ));
        
        // Métodos y Headers
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
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