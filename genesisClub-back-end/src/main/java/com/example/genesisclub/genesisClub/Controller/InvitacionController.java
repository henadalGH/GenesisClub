package com.example.genesisclub.genesisClub.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.Responce.InvitacionResponseDTO; // Verifica que el paquete sea Responce o Response
import com.example.genesisclub.genesisClub.Servicio.InvitacionService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/invitacion")
public class InvitacionController {

    @Autowired
    private InvitacionService invitacionService; 

    /**
     * Crea una nueva invitación y envía el correo automáticamente.
     * Acceso: Solo SOCIOS o ADMINS logueados.
     * @throws MessagingException 
     */
    @PreAuthorize("hasRole('SOCIO')")
    @PostMapping("/crear/{socioId}")
    public ResponseEntity<InvitacionResponseDTO> crearInvitacion(
            @PathVariable Long socioId,
            @RequestBody InvitacionRequestDTO dto) throws MessagingException {

        // Al especificar <InvitacionResponseDTO>, eliminamos el "?"
        return ResponseEntity.ok(invitacionService.crearInvitacion(socioId, dto));
    }

    /**
     * Valida el token cuando el invitado hace clic en el enlace.
     * Acceso: Público (no requiere @Secured porque el invitado aún no tiene cuenta).
     */
    @GetMapping("/aceptar/{token}")
    public ResponseEntity<InvitacionResponseDTO> aceptarInvitacion(@PathVariable String token) {
        
        // Este método cambia el estado a ACEPTADA y valida expiración
        return ResponseEntity.ok(invitacionService.aceptarInvitacion(token));
    }
}