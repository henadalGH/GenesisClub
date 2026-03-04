package com.example.genesisclub.genesisClub.seguridad;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTUtilityService jwtUtilityService;
    private final UsuarioRepository usuarioRepository;

    public JWTAuthorizationFilter(JWTUtilityService jwtUtilityService, UsuarioRepository usuarioRepository) {
        this.jwtUtilityService = jwtUtilityService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String header = request.getHeader("Authorization");

            // ✅ SIN TOKEN → dejar pasar (rutas públicas funcionan)
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.replace("Bearer ", "");

            // ✅ token inválido → dejar pasar (Spring decide)
            if (!jwtUtilityService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 🔥 AUTENTICAR USUARIO
            // ===============================
            Long userId = jwtUtilityService.getUserId(token);

            UsuarioEntity usuario = usuarioRepository.findById(userId).orElse(null);

            if (usuario != null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                Collections.emptyList() // si tenés roles, acá van
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            // 🔥 NUNCA cortar acá
            // Spring manejará el 401 si hace falta
        }

        filterChain.doFilter(request, response);
    }
}