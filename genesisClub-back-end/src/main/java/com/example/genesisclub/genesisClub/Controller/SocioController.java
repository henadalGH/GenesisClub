package com.example.genesisclub.genesisClub.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.SocioDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.VehiculoDTO;
import com.example.genesisclub.genesisClub.Servicio.SocioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/socio")
public class SocioController {

    @Autowired
    private SocioServicio socioServicio;

    @GetMapping("/todos")
    public List<SocioDTO> obtenerSocios() {
        return socioServicio.obtenerSocio();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocioDTO> getSocioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(socioServicio.obtenerPorId(id));
    }

    @GetMapping("/{id}/vehiculos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosSocio(@PathVariable Long id) {
        return ResponseEntity.ok(socioServicio.obtenerVehiculosPorSocio(id));
    }

    @PutMapping("/{id}/suspender")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> suspenderSocio(@PathVariable Long id) {
        try {
            socioServicio.suspenderSocio(id);
            return ResponseEntity.ok("Socio suspendido correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> bloquearSocio(@PathVariable Long id) {
        try {
            socioServicio.bloquearSocio(id);
            return ResponseEntity.ok("Socio bloqueado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activarSocio(@PathVariable Long id) {
        try {
            socioServicio.activarSocio(id);
            return ResponseEntity.ok("Socio activado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}