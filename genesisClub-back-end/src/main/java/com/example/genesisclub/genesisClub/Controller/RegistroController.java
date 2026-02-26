package com.example.genesisclub.genesisClub.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Servicio.RegistroUsuarioServicio;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/usuario")
public class RegistroController {

    @Autowired
    private RegistroUsuarioServicio registroService;

    @PostMapping("/registro")
    public ResponseEntity<ResponceDTO> registrar(@RequestBody RegistroDTO dto) {
        return ResponseEntity.ok(registroService.registrarUsuario(dto));
    }
}
