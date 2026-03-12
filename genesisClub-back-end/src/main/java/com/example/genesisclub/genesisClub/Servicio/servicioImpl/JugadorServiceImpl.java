package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.JugadorDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.VehiculoDTO;
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

    // ===============================
    // OBTENER VEHÍCULOS POR JUGADOR
    // ===============================
    @Override
    public List<VehiculoDTO> obtenerVehiculosPorJugador(Long id) {
        JugadorEntity jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con id: " + id));
        
        return jugador.getUsuario().getVehiculos()
                .stream()
                .map(this::vehiculoToDTO)
                .toList();
    }

    // ===============================
    // SUSPENDER JUGADOR
    // ===============================
    @Override
    @Transactional
    public void suspenderJugador(Long id) {
        JugadorEntity jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con id: " + id));
        
        // Cambiar estado del usuario a SUSPENDIDO
        jugador.getUsuario().setEstado("SUSPENDIDO");
        jugadorRepository.save(jugador);
    }

    // ===============================
    // BLOQUEAR JUGADOR
    // ===============================
    @Override
    @Transactional
    public void bloquearJugador(Long id) {
        JugadorEntity jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con id: " + id));
        
        // Cambiar estado del usuario a BLOQUEADO
        jugador.getUsuario().setEstado("BLOQUEADO");
        jugadorRepository.save(jugador);
    }

    // ===============================
    // ACTIVAR JUGADOR
    // ===============================
    @Override
    @Transactional
    public void activarJugador(Long id) {
        JugadorEntity jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado con id: " + id));
        
        // Cambiar estado del usuario a ACTIVO
        jugador.getUsuario().setEstado("ACTIVO");
        jugadorRepository.save(jugador);
    }

    private JugadorDTO mapToDTO(JugadorEntity entity) {
        JugadorDTO dto = new JugadorDTO();
        dto.setId(entity.getId());
        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
            dto.setNombre(entity.getUsuario().getNombre());
            dto.setApellido(entity.getUsuario().getApellido());
            dto.setEmail(entity.getUsuario().getEmail());
            dto.setEstado(entity.getUsuario().getEstado() != null ? 
                         entity.getUsuario().getEstado() : "ACTIVO");
            String email = entity.getUsuario().getEmail();
            solicitudRepository.findTopByEmailOrderByFechaSolicitudDesc(email)
                    .map(sol -> sol.getVehiculo())
                    .filter(v -> v != null)
                    .ifPresent(v -> dto.setPatente(v.getPatente()));
        }
        return dto;
    }

    private VehiculoDTO vehiculoToDTO(com.example.genesisclub.genesisClub.Modelo.Entidad.VehiculoEntity v) {
        return new VehiculoDTO(
                v.getId(),
                v.getPatente(),
                v.getMarca(),
                v.getModelo(),
                v.getAnio(),
                v.isTieneGnc()
        );
    }
}
