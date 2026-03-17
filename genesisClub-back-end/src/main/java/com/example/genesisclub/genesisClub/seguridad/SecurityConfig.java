package com.example.genesisclub.genesisClub.seguridad;

import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/usuario/registro").permitAll()
                .requestMatchers("/api/solicitud/socio/nuevo").permitAll()
                .requestMatchers("/api/solicitud/socio/registro-invitado").permitAll()
                .requestMatchers("/api/solicitud/jugador").permitAll()
                .requestMatchers("/email/**").permitAll()
                .requestMatchers("/publico/test-final").permitAll()
                .requestMatchers("/api/invitacion/aceptar/**").permitAll()
                .requestMatchers("/api/rubro/activos").permitAll()
                .requestMatchers("/api/rubro/buscar").permitAll()
                .requestMatchers("/api/rubro/nombre/**").permitAll()
                .requestMatchers("/api/rubro/clave/**").permitAll()

                // --- RUTAS DE ADMINISTRADOR ---
                .requestMatchers("/api/solicitud/socio/pendientes").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/socio/actualizar/**").hasRole("ADMIN")
                // endpoints de solicitudes de jugadores (nuevo controlador separado)
                .requestMatchers("/api/solicitud/jugador/pendientes").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/jugador/actualizar/**").hasRole("ADMIN")

                // acceso a recursos de jugadores
                .requestMatchers("/api/jugador/todos").hasRole("ADMIN")
                .requestMatchers("/api/jugador/**").hasRole("ADMIN")

                .requestMatchers("/api/socio/todos").hasRole("ADMIN")
                .requestMatchers("/api/socio/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/admin/solo").hasRole("ADMIN")

                // --- RUTAS DE RUBROS - ADMINISTRADOR ---
                .requestMatchers("/api/rubro").hasRole("ADMIN")
                .requestMatchers("/api/rubro/**").hasRole("ADMIN")
                .requestMatchers("/api/rubro-socio").hasRole("ADMIN")
                .requestMatchers("/api/rubro-socio/**").hasRole("ADMIN")
                .requestMatchers("/api/historial-rubro").hasRole("ADMIN")
                .requestMatchers("/api/historial-rubro/**").hasRole("ADMIN")

                // --- RUTAS DE USUARIO-RUBRO - SOCIO/ADMIN ---
                .requestMatchers("/api/usuario-rubro").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers("/api/usuario-rubro/**").hasAnyRole("SOCIO", "ADMIN")

                // --- RUTAS DE ACCESO LOG RUBROS - ADMINISTRADOR ---
                .requestMatchers("/api/rubro-acceso-log").hasRole("ADMIN")
                .requestMatchers("/api/rubro-acceso-log/**").hasRole("ADMIN")

                // --- RUTAS DEL CONTROLLER RELACION-SOCIO ---
                .requestMatchers("/api/relacion-socio/mis-invitados/**").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers("/api/relacion-socio/mi-red-arbol/**").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers("/api/relacion-socio/mi-red-lista/**").hasAnyRole("SOCIO", "ADMIN")

                // --- OTRAS RUTAS AUTENTICADAS ---
                .requestMatchers("/api/invitacion/**").hasAnyRole("SOCIO", "ADMIN")
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
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
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