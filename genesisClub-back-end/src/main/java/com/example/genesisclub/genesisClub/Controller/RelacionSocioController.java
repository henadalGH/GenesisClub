package com.example.genesisclub.genesisClub.Controller;

import com.example.genesisclub.genesisClub.Modelo.DTO.RelacionSocioDTO;
import com.example.genesisclub.genesisClub.Servicio.RelacionSocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relacion-socio")
@CrossOrigin(origins = "*") // Para conectar con tu Angular sin problemas de CORS
public class RelacionSocioController {

    @Autowired
    private RelacionSocioService relacionService;

    // 1. Obtener solo los invitados directos (Nivel 1)
    // Este usa el método obtenerReferidosDeSocio(id) que agregamos al Service
    @GetMapping("/mis-invitados/{id}")
    public ResponseEntity<List<RelacionSocioDTO>> listarMisInvitados(@PathVariable Long id) {
        List<RelacionSocioDTO> invitados = relacionService.obtenerReferidosDeSocio(id);
        return ResponseEntity.ok(invitados);
    }

    // 2. Obtener TODA la red hacia abajo en forma de Árbol
    // Ideal para ver la estructura completa de GenesisClub
    @GetMapping("/mi-red-arbol/{id}")
    public ResponseEntity<RelacionSocioDTO> verMiRedCompleta(@PathVariable Long id) {
        RelacionSocioDTO arbol = relacionService.obtenerArbolPorSocio(id);
        return ResponseEntity.ok(arbol);
    }

    // 3. Opcional: Obtener toda la red pero en una lista plana
    @GetMapping("/mi-red-lista/{id}")
    public ResponseEntity<List<RelacionSocioDTO>> listarTodaLaRedPlana(@PathVariable Long id) {
        return ResponseEntity.ok(relacionService.obtenerListaDescendientes(id));
    }
}