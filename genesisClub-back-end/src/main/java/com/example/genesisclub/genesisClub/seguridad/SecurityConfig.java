package com.example.genesisclub.genesisClub.seguridad;

import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            // 1. ELIMINAR EL BLOQUEO DE NAVEGADOR (CORS)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // 2. PERMITIR "PREFLIGHT" (Lo que fallaba en tu imagen)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 3. RUTAS LIBRES (Con comodines para que no falle por una barra)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/usuario/registro/**").permitAll()
                .requestMatchers("/api/solicitud/nuevo/**").permitAll()
                .requestMatchers("/email/enviar/**").permitAll()
                
                // 4. SEGURIDAD PARA EL RESTO
                .requestMatchers("/api/solicitud/pendientes").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 5. TU FILTRO JWT
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Permitimos todo para que Render deje de rebotar la conexión
        config.setAllowedOriginPatterns(List.of("*")); 
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}