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
                // Nota: "/api/rubro/nombre/**" y "/api/rubro/clave/**" están bien porque el ** está al final
                .requestMatchers("/api/rubro/activos", "/api/rubro/buscar", "/api/rubro/nombre/**", "/api/rubro/clave/**").permitAll()
                .requestMatchers("/publico/**").permitAll()

                // --- RUTAS DE ADMINISTRADOR ---
                .requestMatchers("/api/solicitud/socio/pendientes", "/api/solicitud/socio/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/jugador/pendientes", "/api/solicitud/jugador/actualizar/**").hasRole("ADMIN")
                
                // --- CORRECCIÓN AQUÍ: Endpoints de Solicitudes Rubro ---
                // Se cambia ** por * porque el ID está en el medio de la ruta
                .requestMatchers(HttpMethod.GET, "/api/solicitudes-rubro/pendientes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes-rubro/*/aprobar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes-rubro/*/rechazar").hasRole("ADMIN")
                
                .requestMatchers("/api/jugador/**", "/api/socio/**", "/api/auth/admin/solo").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/rubro/**", "/api/historial-rubro/**", "/api/rubro-acceso-log/**").hasAnyRole("ADMIN", "SOCIO")
                .requestMatchers(HttpMethod.GET, "/api/rubro-socio/**").hasAnyRole("ADMIN", "SOCIO")
                .requestMatchers(HttpMethod.POST, "/api/rubro-socio/socio/**").hasAnyRole("ADMIN", "SOCIO")
                .requestMatchers(HttpMethod.DELETE, "/api/rubro-socio/**").hasAnyRole("ADMIN", "SOCIO")
                .requestMatchers(HttpMethod.PUT, "/api/rubro-socio/**").hasRole("ADMIN")

                // --- RUTAS DE SOCIO / MIXTAS ---
                .requestMatchers(HttpMethod.POST, "/api/solicitudes-rubro").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/invitacion/crear/**").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/invitacion/**").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers("/api/usuario-rubro/**", "/api/relacion-socio/**").hasAnyRole("SOCIO", "ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(new JWTAuthorizationFilter(jwtUtilityService), 
                            UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200", 
                "https://*.onrender.com"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
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