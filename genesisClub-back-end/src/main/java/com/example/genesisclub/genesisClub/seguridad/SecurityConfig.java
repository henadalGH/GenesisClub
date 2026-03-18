package com.example.genesisclub.genesisClub.seguridad;

import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTUtilityService jwtUtilityService;

    public SecurityConfig(JWTUtilityService jwtUtilityService) {
        this.jwtUtilityService = jwtUtilityService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // --- RUTAS PÚBLICAS ---
                .requestMatchers("/api/auth/**", "/api/usuario/registro", "/email/**").permitAll()
                .requestMatchers("/api/solicitud/socio/nuevo", "/api/solicitud/socio/registro-invitado").permitAll()
                .requestMatchers("/api/solicitud/jugador", "/api/invitacion/aceptar/**").permitAll()
                .requestMatchers("/api/rubro/activos", "/api/rubro/buscar", "/api/rubro/nombre/**", "/api/rubro/clave/**").permitAll()
                .requestMatchers("/publico/**").permitAll()

                // --- RUTAS DE ADMINISTRADOR ---
                // Al usar .hasRole("ADMIN"), Spring busca internamente "ROLE_ADMIN"
                // Esto coincide perfectamente con tu JWTAuthorizationFilter
                .requestMatchers("/api/solicitud/socio/pendientes", "/api/solicitud/socio/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/jugador/pendientes", "/api/solicitud/jugador/actualizar/**").hasRole("ADMIN")
                
                // Endpoints de Solicitudes Rubro (el del error 403)
                .requestMatchers(HttpMethod.GET, "/api/solicitudes-rubro/pendientes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes-rubro/**/aprobar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes-rubro/**/rechazar").hasRole("ADMIN")
                
                .requestMatchers("/api/jugador/**", "/api/socio/**", "/api/auth/admin/solo").hasRole("ADMIN")
                .requestMatchers("/api/rubro/**", "/api/rubro-socio/**", "/api/historial-rubro/**", "/api/rubro-acceso-log/**").hasRole("ADMIN")

                // --- RUTAS DE SOCIO / MIXTAS ---
                .requestMatchers(HttpMethod.POST, "/api/solicitudes-rubro").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers("/api/usuario-rubro/**", "/api/relacion-socio/**", "/api/invitacion/**").hasAnyRole("SOCIO", "ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(new JWTAuthorizationFilter(jwtUtilityService), 
                            UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configuración de orígenes para local y producción
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200", 
                "https://*.onrender.com"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // IMPORTANTE: Permitir todos los headers para evitar el 403 en peticiones OPTIONS
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Exponer Authorization para que Angular pueda leerlo si es necesario
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}