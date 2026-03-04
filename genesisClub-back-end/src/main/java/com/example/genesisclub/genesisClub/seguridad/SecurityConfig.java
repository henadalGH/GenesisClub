package com.example.genesisclub.genesisClub.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    // ✅ RECOMENDACIÓN: Inyectar vía constructor o directamente en el Bean
    @Autowired
    private JWTUtilityService jwtUtilityService;

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return new JWTAuthorizationFilter(jwtUtilityService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.disable()) // Render a veces se pelea con esto si el front es local
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // PÚBLICAS
                .requestMatchers("/api/auth/**", "/api/usuario/registro", "/api/solicitud/nuevo", "/email/**", "/api/invitacion/aceptar/**").permitAll()
                // ADMIN
                .requestMatchers("/api/solicitud/pendientes", "/api/solicitud/actualizar/**", "/api/socio/**").hasRole("ADMIN")
                // AUTH
                .requestMatchers("/api/invitacion/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, authEx) -> {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
            }))
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}