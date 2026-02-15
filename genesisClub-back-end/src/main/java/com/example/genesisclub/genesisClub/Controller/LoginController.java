package com.example.genesisclub.genesisClub.Controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.genesisclub.genesisClub.Modelo.DTO.LoginDTO;
import com.example.genesisclub.genesisClub.Servicio.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    // ======================================================
    // 🔥 LOGIN
    // ======================================================
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginDTO loginRequest
    ) throws Exception {

        Map<String, Object> response = authService.login(loginRequest);

        boolean success = (boolean) response.get("success");

        if (success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ======================================================
    // 🔥 ENDPOINT SOLO ADMIN (TEST)
    // ======================================================
    @GetMapping("/admin/solo")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> soloAdmin() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> data = new HashMap<>();
        data.put("usuario", auth.getName());
        data.put("roles", auth.getAuthorities());

        return data;
    }

    // ======================================================
    // 🔥 LOGOUT
    // ======================================================
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no proporcionado");
        }

        String token = authHeader.substring(7);

        authService.logout(token);

        return ResponseEntity.ok("Sesión cerrada con éxito");
    }
}
