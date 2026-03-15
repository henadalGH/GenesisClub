package com.example.genesisclub.genesisClub.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudJugadorDTO;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Servicio.SolicitudJugadorService;

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
@RequestMapping("/api/solicitud/jugador")
public class SolicitudJugadorController {

    @Autowired
    private SolicitudJugadorService solicitudJugadorService;

    // ======================================================
    // REGISTRO JUGADOR (PÚBLICO)
    // ======================================================
    @PostMapping
    public ResponseEntity<ResponceDTO> crearSolicitudJugador( @Valid @RequestBody SolicitudJugadorDTO solicitud) {
        ResponceDTO response = solicitudJugadorService.solicitarJugador(solicitud);

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
    public ResponseEntity<List<SolicitudJugadorDTO>> obtenerSolicitudesPendientes() {
        List<SolicitudJugadorDTO> pendientes = solicitudJugadorService.obtenerSolicitudesPendientesJugador();
        return new ResponseEntity<>(pendientes, HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponceDTO> actualizarEstadoSolicitud(
            @PathVariable Long id,
            @RequestParam EstadoSolicitudEnums nuevoEstado
    ) {
        ResponceDTO response = solicitudJugadorService.actualizarEstadoSolicitudJugador(id, nuevoEstado);
        return ResponseEntity.ok(response);
    }
}
