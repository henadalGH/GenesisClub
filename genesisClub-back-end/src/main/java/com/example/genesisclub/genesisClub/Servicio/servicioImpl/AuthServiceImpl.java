package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.DTO.LoginDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
import com.example.genesisclub.genesisClub.Repositorio.AdminRepository;
import com.example.genesisclub.genesisClub.Repositorio.JugadorRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.AuthService;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JWTUtilityService jwtUtilityService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ======================================================
    // LOGIN
    // ======================================================
    @Override
    public Map<String, Object> login(LoginDTO login) throws Exception {

        Map<String, Object> response = new HashMap<>();
        
        // Mensaje genérico para CUALQUIER error de autenticación
        final String ERROR_MESSAGE = "Usuario o contraseña incorrectos";

        UsuarioEntity usuario = usuarioRepository
                .findByEmail(login.getEmail())
                .orElse(null);

        // ❌ no existe o ❌ password incorrecta → MISMO mensaje
        if (usuario == null || !encoder.matches(login.getPassword(), usuario.getPassword())) {
            response.put("success", false);
            response.put("message", ERROR_MESSAGE);
            return response;
        }

        // ✅ OBTENER ROL DESDE TU ENTIDAD
        String rolNombre = usuario.getRol().getNombre().name();

        // ✅ DETERMINAR QUÉ ID DEBE USARSE EN EL TOKEN
        // (usuario.id es el id de la tabla usuario;
        //  para SOCIO/JUGADOR/ADMIN necesitamos el id de la tabla relacionada)
        Long tokenSubjectId = usuario.getId();
        switch (rolNombre) {
            case "SOCIO" -> {
                tokenSubjectId = socioRepository.findByUsuario_Id(usuario.getId())
                        .map(s -> s.getId())
                        .orElse(usuario.getId());
            }
            case "JUGADOR" -> {
                tokenSubjectId = jugadorRepository.findByUsuario_Id(usuario.getId())
                        .map(j -> j.getId())
                        .orElse(usuario.getId());
            }
            case "ADMIN" -> {
                tokenSubjectId = adminRepository.findByUsuario_Id(usuario.getId())
                        .map(a -> a.getId())
                        .orElse(usuario.getId());
            }
        }

        // ✅ GENERAR JWT CON EL ID CORRECTO
        String token = jwtUtilityService.generateJWT(
                tokenSubjectId,
                rolNombre
        );

        // ✅ RESPUESTA LIMPIA
        response.put("success", true);
        response.put("token", token);
        response.put("rol", rolNombre); // opcional (útil para frontend)
        response.put("email", usuario.getEmail());
        response.put("userId", usuario.getId());
        response.put("profileId", tokenSubjectId); // id real del rol (socio/jugador/admin)

        return response;
    }


    // ======================================================
    // LOGOUT (blacklist simple)
    // ======================================================
    private final Map<String, String> revokedTokens = new HashMap<>();

    @Override
    public void logout(String token) {
        revokedTokens.put(token, "revoked");
    }

    public boolean isRevoked(String token) {
        return revokedTokens.containsKey(token);
    }
}
