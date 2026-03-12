package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.JugadorDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.JugadorEntity;
import com.example.genesisclub.genesisClub.Repositorio.JugadorRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Servicio.JugadorService;

@Service
@Transactional(readOnly = true)
public class JugadorServiceImpl implements JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private SolicitudReposistory solicitudRepository;

    @Override
    public List<JugadorDTO> obtenerTodosLosJugadores() {
        return jugadorRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public JugadorDTO obtenerJugadorPorId(Long id) {
        JugadorEntity jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con id: " + id));
        return mapToDTO(jugador);
    }

    private JugadorDTO mapToDTO(JugadorEntity entity) {
        JugadorDTO dto = new JugadorDTO();
        dto.setId(entity.getId());
        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
            dto.setNombre(entity.getUsuario().getNombre());
            dto.setApellido(entity.getUsuario().getApellido());
            dto.setEmail(entity.getUsuario().getEmail());
            dto.setEstado("ACTIVO");
            // buscar patente en la última solicitud por email
            String email = entity.getUsuario().getEmail();
            solicitudRepository.findTopByEmailOrderByFechaSolicitudDesc(email)
                    .map(sol -> sol.getVehiculo())
                    .filter(v -> v != null)
                    .ifPresent(v -> dto.setPatente(v.getPatente()));
        }
        return dto;
    }
}
