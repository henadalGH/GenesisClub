        package com.example.genesisclub.genesisClub.seguridad;

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
        @EnableMethodSecurity
        public class SecurityConfig {

        private final JWTUtilityService jwtUtilityService;
        private final UsuarioRepository usuarioRepository;

        public SecurityConfig(
                JWTUtilityService jwtUtilityService,
                UsuarioRepository usuarioRepository) {
                this.jwtUtilityService = jwtUtilityService;
                this.usuarioRepository = usuarioRepository;
        }

        // =========================================================
        // 🔥 JWT FILTER
        // =========================================================
        @Bean
        public JWTAuthorizationFilter jwtAuthorizationFilter() {
                return new JWTAuthorizationFilter(jwtUtilityService, usuarioRepository);
        }

        // =========================================================
        // 🔥 SECURITY PRINCIPAL
        // =========================================================
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                // CORS
                .cors(Customizer.withDefaults())

                // SIN CSRF (API REST)
                .csrf(csrf -> csrf.disable())

                // Stateless (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // =================================================
                // 🔥 AUTORIZACIÓN
                // =================================================
                .authorizeHttpRequests(auth -> auth

                        // preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ===============================
                        // 🔓 PUBLICAS
                        // ===============================
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/usuario/registro",
                                "/api/solicitud/nuevo/**",
                                "/email/**",
                                "/api/invitacion/aceptar/**"
                        ).permitAll()

                        // ===============================
                        // 🔐 ADMIN
                        // ===============================
                        .requestMatchers("/api/solicitud/pendientes/**").hasRole("ADMIN")
                        .requestMatchers("/api/solicitud/actualizar/**").hasRole("ADMIN")
                        .requestMatchers("/api/socio/**").hasRole("ADMIN")

                        // ===============================
                        // 🔐 LOGUEADOS
                        // ===============================
                        .requestMatchers("/api/invitacion/**").authenticated()

                        // resto protegido
                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(
                        jwtAuthorizationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )

                // 401 custom
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint((req, res, e) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado")
                        )
                );

                return http.build();
        }

        // =========================================================
        // 🔥 PASSWORD
        // =========================================================
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
        }