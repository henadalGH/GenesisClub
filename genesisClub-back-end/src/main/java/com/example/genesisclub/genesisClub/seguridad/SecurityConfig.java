package com.example.genesisclub.genesisClub.seguridad;

import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
public class SecurityConfig {

    private final JWTUtilityService jwtUtilityService;

    public SecurityConfig(JWTUtilityService jwtUtilityService) {
        this.jwtUtilityService = jwtUtilityService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. ⚡ ACTIVAR CORS (Esto es vital para que use el bean de abajo)
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/usuario/registro").permitAll()
                .requestMatchers("/api/solicitud/nuevo").permitAll()
                .requestMatchers("/api/solicitud/registro-invitado").permitAll()
                .requestMatchers("/email/**").permitAll()
                .requestMatchers("/api/invitacion/aceptar/**").permitAll()

                .requestMatchers("/api/solicitud/pendientes").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/api/socio/todos").hasRole("ADMIN")
                .requestMatchers("/api/socio/**").hasRole("ADMIN")
                .requestMatchers("/api/invitacion/**").authenticated()
            )
            .addFilterBefore(new JWTAuthorizationFilter(jwtUtilityService), 
                            UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. ⚡ CONFIGURACIÓN DE CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Definimos los orígenes permitidos
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200", 
                "https://*.onrender.com"
        ));
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Cabeceras permitidas (importante incluir Authorization para JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        // Permitir envío de cookies/auth headers
        configuration.setAllowCredentials(true);
        // Cache de la respuesta preflight (1 hora)
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