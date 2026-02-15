package com.example.genesisclub.genesisClub.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Servicio.RegistroUsuarioServicio;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/registro")
public class RegistroController {

    @Autowired
    private RegistroUsuarioServicio registroService;

    @PostMapping("/nuevo")
public ResponseEntity<ResponceDTO> nuevoUsuario(
        @RequestBody RegistroDTO registroDTO,
        @RequestParam RolesEnums rolesEnum,
        @RequestParam(required = false) EstadoSocioEnums estado
    ) throws Exception {

    return new ResponseEntity<>(
            registroService.registrarUsuario(registroDTO, rolesEnum, estado),
            HttpStatus.CREATED
    );
}

}
