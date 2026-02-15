package com.example.genesisclub.genesisClub.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Servicio.InvitacionService;

@RestController
@RequestMapping("/api/invitacion")
public class InvitacionController {

    @Autowired
    private InvitacionService invitacionService;

    // SOLO SOCIOS LOGUEADOS
    @Secured({"ROLE_SOCIO","ROLE_ADMIN"})
    @PostMapping("/crear/{socioId}")
    public ResponseEntity<?> crear(
            @PathVariable Long socioId,
            @RequestBody InvitacionRequestDTO dto) {

        return ResponseEntity.ok(
                invitacionService.crearInvitacion(socioId, dto)
        );
    }
}
