package com.example.genesisclub.genesisClub.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudDTO;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Servicio.SolicitudSerSocioService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/solicitud/socio")
public class SolicitudSocioController {

    @Autowired
    private SolicitudSerSocioService solicitudSerSocioService;

    // ======================================================
    // REGISTRO NORMAL (PÚBLICO)
    // ======================================================
    @PostMapping("/nuevo")
    public ResponseEntity<ResponceDTO> crearSolicitud(@Valid @RequestBody SolicitudDTO solicitud) {
        ResponceDTO response = solicitudSerSocioService.crearSolicitud(solicitud, null);

        if (response.getNumOfErrors() > 0) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ======================================================
    // REGISTRO POR INVITACIÓN (PÚBLICO)
    // ======================================================
    @PostMapping("/registro-invitado")
    public ResponseEntity<ResponceDTO> registrarConInvitacion(
            @RequestBody SolicitudDTO solicitud,
            @RequestParam String token) { // Recibe el token desde la URL (?token=...)

        ResponceDTO response = solicitudSerSocioService.crearSolicitudDesdeInvitacion(solicitud, token);

        if (response.getNumOfErrors() > 0) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ======================================================
    // GESTIÓN DEL ADMINISTRADOR (PROTEGIDO)
    // ======================================================
    
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SolicitudDTO>> obtenerSolicitudesPendientes() {
        List<SolicitudDTO> pendientes = solicitudSerSocioService.obtenerSolicitudesPendientes();
        return new ResponseEntity<>(pendientes, HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponceDTO> actualizarEstadoSolicitud(
            @PathVariable Long id,
            @RequestParam EstadoSolicitudEnums nuevoEstado
    ) {
        ResponceDTO response = solicitudSerSocioService.actualizarEstadoSolicitud(id, nuevoEstado);
        return ResponseEntity.ok(response);
    } 
}