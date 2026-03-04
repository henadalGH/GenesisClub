package com.example.genesisclub.genesisClub.seguridad;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTUtilityService jwtUtilityService;
    private final UsuarioRepository usuarioRepository;

    // ✅ Pasamos ambos al constructor para que funcionen siempre en Render
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

        String header = request.getHeader("Authorization");

        // ✅ Filtramos la "basura" de Angular
        if (header == null || !header.startsWith("Bearer ") || 
            header.equalsIgnoreCase("Bearer null") || 
            header.equalsIgnoreCase("Bearer undefined")) {
            
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            JWTClaimsSet claims = jwtUtilityService.parseJWT(token);
            Long userId = Long.parseLong(claims.getSubject());
            
            // Usamos el repositorio pasado por el constructor
            UsuarioEntity usuario = usuarioRepository.findById(userId).orElse(null);

            if (usuario != null) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                usuario.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (Exception e) {
            // Si el token falla, limpiamos y seguimos (permitiendo rutas permitAll)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}