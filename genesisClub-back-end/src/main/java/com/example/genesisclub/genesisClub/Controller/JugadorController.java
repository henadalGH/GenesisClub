package com.example.genesisclub.genesisClub.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.JugadorDTO;
import com.example.genesisclub.genesisClub.Servicio.JugadorService;

@RestController
@RequestMapping("/api/jugador")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    // ======================================================
    // GESTIÓN DEL ADMINISTRADOR (PROTEGIDO)
    // ======================================================

    /**
     * Obtiene la lista de todos los jugadores registrados
     * Acceso: Solo ADMIN
     */
    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JugadorDTO>> obtenerTodosLosJugadores() {
        List<JugadorDTO> jugadores = jugadorService.obtenerTodosLosJugadores();
        return new ResponseEntity<>(jugadores, HttpStatus.OK);
    }

    /**
     * Obtiene los detalles de un jugador específico por ID
     * Acceso: Solo ADMIN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JugadorDTO> obtenerJugadorPorId(@PathVariable Long id) {
        JugadorDTO jugador = jugadorService.obtenerJugadorPorId(id);
        return new ResponseEntity<>(jugador, HttpStatus.OK);
    }
}