package com.example.genesisclub.genesisClub.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Servicio.UsuarioServicio;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    
    @Autowired
    private UsuarioServicio usuarioService;

    @GetMapping("/provincia/{provincia}")
    public ResponseEntity<?> porProvincia(@PathVariable String provincia) {
        return ResponseEntity.ok(usuarioService.buscarPorProvincia(provincia));
    }

    @GetMapping("/zona/{zona}")
    public ResponseEntity<?> porZona(@PathVariable String zona) {
        return ResponseEntity.ok(usuarioService.buscarPorZona(zona));
    }
}