package com.example.genesisclub.genesisClub.seguridad;

import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JWTUtilityService jwtUtilityService;

    public SecurityConfig(JWTUtilityService jwtUtilityService) {
        this.jwtUtilityService = jwtUtilityService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // rutas públicas
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/usuario/registro").permitAll()
                .requestMatchers("/api/solicitud/nuevo").permitAll()
                .requestMatchers("/api/solicitud/registro-invitado").permitAll()
                .requestMatchers("/email/**").permitAll()
                .requestMatchers("/api/invitacion/aceptar/**").permitAll()

                // rutas protegidas
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

    // Para poder inyectar AuthenticationManager si lo necesitás
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 🔑 Bean que faltaba para que Spring pueda inyectar PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}