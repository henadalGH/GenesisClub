package com.example.genesisclub.genesisClub.Controller;

import com.example.genesisclub.genesisClub.Modelo.DTO.AdminResumenDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.EstadoSolicitudRubro;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.TipoSolicitud;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.JugadorRepository;
import com.example.genesisclub.genesisClub.Repositorio.RubroRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudRubroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private SolicitudReposistory solicitudRepository;

    @Autowired
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @Autowired
    private RubroRepository rubroRepository;

    @Autowired
    private SolicitudRubroRepository solicitudRubroRepository;

    @GetMapping("/resumen")
    public ResponseEntity<AdminResumenDTO> obtenerResumen() {
        EstadoSolicitudEntity estadoPendiente = estadoSolicitudRepository.findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE no encontrado"));

        long solicitudesPendientes = solicitudRepository.findByEstado(estadoPendiente).size();
        long solicitudesJugadoresPendientes = solicitudRepository.findByEstadoAndTipoSolicitud(estadoPendiente, TipoSolicitud.JUGADOR).size();
        long solicitudesRubrosPendientes = solicitudRubroRepository.findAll().stream()
                .filter(s -> s.getEstado() == EstadoSolicitudRubro.PENDIENTE)
                .count();

        AdminResumenDTO resumen = new AdminResumenDTO(
                socioRepository.count(),
                jugadorRepository.count(),
                solicitudesPendientes,
                solicitudesJugadoresPendientes,
                solicitudesRubrosPendientes,
                rubroRepository.findByActivo(true).size()
        );

        return ResponseEntity.ok(resumen);
    }
}
