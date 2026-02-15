package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.DTO.LoginDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.AuthService;
import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JWTUtilityService jwtUtilityService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ======================================================
    // LOGIN
    // ======================================================
    @Override
    public Map<String, Object> login(LoginDTO login) throws Exception {

        Map<String, Object> response = new HashMap<>();

        UsuarioEntity usuario = usuarioRepository
                .findByEmail(login.getEmail())
                .orElse(null);

        // ❌ no existe
        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return response;
        }

        // ❌ password incorrecta
        if (!encoder.matches(login.getPassword(), usuario.getPassword())) {
            response.put("success", false);
            response.put("message", "Contraseña incorrecta");
            return response;
        }

        // ✅ OBTENER ROL DESDE TU ENTIDAD
        String rolNombre = usuario.getRol().getNombre().name();

        // ✅ GENERAR JWT CON ROL
        String token = jwtUtilityService.generateJWT(
                usuario.getId(),
                rolNombre
        );

        // ✅ RESPUESTA LIMPIA
        response.put("success", true);
        response.put("token", token);
        response.put("rol", rolNombre); // opcional (útil para frontend)
        response.put("email", usuario.getEmail());
        response.put("userId", usuario.getId());

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
