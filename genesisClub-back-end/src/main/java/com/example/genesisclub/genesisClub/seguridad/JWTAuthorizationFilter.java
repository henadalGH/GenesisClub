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
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getRequestURI();
    String method = request.getMethod();

    // 1. 🚀 DEJAR PASAR PETICIONES "OPTIONS" (CORS PREFLIGHT)
    // Si no hacés esto, Angular nunca va a poder hacer el POST
    if ("OPTIONS".equalsIgnoreCase(method)) {
        response.setStatus(HttpServletResponse.SC_OK);
        return;
    }

    // 2. 🚀 CLÁUSULA DE ESCAPE PARA RUTAS PÚBLICAS
    if (path.contains("/api/solicitud/nuevo") || path.contains("/api/auth") || path.contains("/api/usuario/registro")) {
        filterChain.doFilter(request, response);
        return;
    }

    String header = request.getHeader("Authorization");

    // 3. ✅ FILTRAR BASURA DE ANGULAR (Bearer null/undefined)
    if (header == null || !header.startsWith("Bearer ") || 
        header.equalsIgnoreCase("Bearer null") || 
        header.equalsIgnoreCase("Bearer undefined")) {
        
        filterChain.doFilter(request, response);
        return;
    }

    // ... resto de tu lógica de token ...
    try {
        String token = header.substring(7);
        JWTClaimsSet claims = jwtUtilityService.parseJWT(token);
        Long userId = Long.parseLong(claims.getSubject());
        UsuarioEntity usuario = usuarioRepository.findById(userId).orElse(null);

        if (usuario != null) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    } catch (Exception e) {
        SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
}
}