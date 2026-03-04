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

    public JWTAuthorizationFilter(JWTUtilityService jwtUtilityService, UsuarioRepository usuarioRepository) {
        this.jwtUtilityService = jwtUtilityService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // ✅ dejar pasar SIEMPRE preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ rutas públicas
        if (path.startsWith("/api/solicitud/nuevo")
                || path.startsWith("/api/auth")
                || path.startsWith("/api/usuario/registro")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // ✅ si no hay token → dejar pasar (Spring decide luego)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            JWTClaimsSet claims = jwtUtilityService.parseJWT(token);

            Long userId = Long.parseLong(claims.getSubject());
            UsuarioEntity usuario = usuarioRepository.findById(userId).orElse(null);

            if (usuario != null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                usuario, null, usuario.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}