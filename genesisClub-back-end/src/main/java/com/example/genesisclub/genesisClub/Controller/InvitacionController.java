package com.example.genesisclub.genesisClub.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.Responce.InvitacionResponseDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Servicio.InvitacionService;

@RestController
@RequestMapping("/api/invitacion")
public class InvitacionController {

    @Autowired
    private InvitacionService invitacionService;

    // ✅ crear invitación (usuario logueado)
    @Secured({"ROLE_SOCIO","ROLE_ADMIN"})
    @PostMapping("/crear")
    public ResponseEntity<InvitacionResponseDTO> crearInvitacion(
            @AuthenticationPrincipal SocioEntity socio,
            @RequestBody InvitacionRequestDTO dto) {

        return ResponseEntity.ok(
                invitacionService.crearInvitacion(socio, dto)
        );
    }

    // ✅ aceptar invitación (público)
    @PostMapping("/aceptar/{token}")
    public ResponseEntity<InvitacionResponseDTO> aceptarInvitacion(
            @PathVariable String token) {

        return ResponseEntity.ok(
                invitacionService.aceptarInvitacion(token)
        );
    }
}