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

    @Autowired
    private JWTUtilityService jwtUtilityService;

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() {
        return new JWTAuthorizationFilter(jwtUtilityService);
    }

    @Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http
            .cors(cors -> {}) // 🔥 HABILITA CORS (OBLIGATORIO)
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(authRequest ->
        authRequest
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // públicas
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/registro/**").permitAll()
                .requestMatchers("/api/solicitud/nuevo").permitAll()

                // protegidas
                .requestMatchers("/api/solicitud/pendientes").hasRole("ADMIN")
                .requestMatchers("/api/solicitud/actualizar/**").hasRole("ADMIN")
                .requestMatchers("/api/invitacion/**").authenticated()

                .anyRequest().authenticated()
)


            .sessionManagement(sessionM ->
                    sessionM.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .addFilterBefore(
                    jwtAuthorizationFilter(),
                    UsernamePasswordAuthenticationFilter.class
            )

            .exceptionHandling(exceptionHandling ->
                    exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
                    })
            )

            .build();
}


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
