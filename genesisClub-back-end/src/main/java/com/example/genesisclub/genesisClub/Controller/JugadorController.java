package com.example.genesisclub.genesisClub.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.JugadorDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.VehiculoDTO;
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

    /**
     * Obtiene los vehículos registrados de un jugador
     * Acceso: Solo ADMIN
     */
    @GetMapping("/{id}/vehiculos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosJugador(@PathVariable Long id) {
        List<VehiculoDTO> vehiculos = jugadorService.obtenerVehiculosPorJugador(id);
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    /**
     * Suspende un jugador
     * Acceso: Solo ADMIN
     */
    @PutMapping("/{id}/suspender")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> suspenderJugador(@PathVariable Long id) {
        try {
            jugadorService.suspenderJugador(id);
            return ResponseEntity.ok("Jugador suspendido correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Bloquea un jugador
     * Acceso: Solo ADMIN
     */
    @PutMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> bloquearJugador(@PathVariable Long id) {
        try {
            jugadorService.bloquearJugador(id);
            return ResponseEntity.ok("Jugador bloqueado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Activa un jugador
     * Acceso: Solo ADMIN
     */
    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activarJugador(@PathVariable Long id) {
        try {
            jugadorService.activarJugador(id);
            return ResponseEntity.ok("Jugador activado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}