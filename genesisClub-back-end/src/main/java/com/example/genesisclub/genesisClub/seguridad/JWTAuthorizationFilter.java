package com.example.genesisclub.genesisClub.seguridad;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTUtilityService jwtUtilityService;

    public JWTAuthorizationFilter(JWTUtilityService jwtUtilityService) {
        this.jwtUtilityService = jwtUtilityService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);

            // Verificar validez del token
            if (!jwtUtilityService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtUtilityService.getUserId(token);
            String rol = jwtUtilityService.getRol(token); // Ej: "SOCIO" o "ADMIN"

            // ⚡ Evitamos duplicar "ROLE_" si ya viene así
            String roleToUse = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleToUse);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            String.valueOf(userId), // principal
                            null,
                            List.of(authority)
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 🔹 Debug: muestra rol que Spring ve
            System.out.println("JWT válido -> userId: " + userId + ", Rol asignado: " + roleToUse);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}