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

    // ✅ Actualizado: Ahora le pasamos el repositorio al filtro
    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return new JWTAuthorizationFilter(jwtUtilityService, usuarioRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // ✅ CAMBIO: Habilitamos CORS por defecto (importante para producción)
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 1. RUTAS PÚBLICAS
                .requestMatchers(
                    "/api/auth/**", 
                    "/api/usuario/registro", 
                    "/api/solicitud/nuevo", 
                    "/email/**", 
                    "/api/invitacion/aceptar/**"
                ).permitAll()

                // 2. RUTAS DE ADMINISTRADOR
                .requestMatchers("/api/solicitud/pendientes").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/api/socio/**").hasRole("ADMIN")

                // 3. RUTAS AUTENTICADAS (Cualquier rol)
                .requestMatchers("/api/invitacion/**").authenticated()

                // 4. RESTO DE LAS PETICIONES
                .anyRequest().authenticated()
            )

            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .addFilterBefore(
                jwtAuthorizationFilter(), 
                UsernamePasswordAuthenticationFilter.class
            )

            .exceptionHandling(ex -> 
                ex.authenticationEntryPoint((req, res, authEx) -> {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
                })
            )

            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}