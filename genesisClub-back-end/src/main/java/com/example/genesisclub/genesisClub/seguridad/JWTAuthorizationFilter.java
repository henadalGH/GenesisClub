package com.example.genesisclub.genesisClub.seguridad;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JWTUtilityService jwtUtilityService;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    // ✅ MEJORA: Validar si el header es nulo, no empieza con Bearer o contiene "null"/"undefined"
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
        // ✅ MEJORA: No lances RuntimeException aquí. 
        // Si el token es inválido, simplemente no autentiques y deja que 
        // SecurityConfig decida si la ruta requiere auth o es permitAll().
        SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
}
}
