package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.SocioDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.VehiculoDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Servicio.SocioServicio;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSocioRepository;

@Service
@Transactional(readOnly = true)
public class ServicioSocioImpl implements SocioServicio {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private SolicitudReposistory solicitudRepository;

    @Autowired
    private EstadoSocioRepository estadoRepository;

    // ===============================
    // LISTAR TODOS
    // ===============================
    @Override
    public List<SocioDTO> obtenerSocio() {

        return socioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ===============================
    // OBTENER POR ID
    // ===============================
    @Override
    public SocioDTO obtenerPorId(Long id) {

        SocioEntity socio = socioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Socio no encontrado con id: " + id)
                );

        return toDTO(socio);
    }

    // ===============================
    // OBTENER VEHÍCULOS POR SOCIO
    // ===============================
    @Override
    public List<VehiculoDTO> obtenerVehiculosPorSocio(Long id) {
        SocioEntity socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + id));
        
        return socio.getUsuario().getVehiculos()
                .stream()
                .map(this::vehiculoToDTO)
                .toList();
    }

    // ===============================
    // SUSPENDER SOCIO
    // ===============================
    @Override
    @Transactional
    public void suspenderSocio(Long id) {
        SocioEntity socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + id));
        
        var estadoSuspendido = estadoRepository.findByEstado(EstadoSocioEnums.SUSPENDIDO)
                .orElseThrow(() -> new RuntimeException("Estado SUSPENDIDO no encontrado"));
        
        socio.setEstado(estadoSuspendido);
        socioRepository.save(socio);
    }

    // ===============================
    // BLOQUEAR SOCIO
    // ===============================
    @Override
    @Transactional
    public void bloquearSocio(Long id) {
        SocioEntity socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + id));
        
        var estadoBloqueado = estadoRepository.findByEstado(EstadoSocioEnums.BLOQUEADO)
                .orElseThrow(() -> new RuntimeException("Estado BLOQUEADO no encontrado"));
        
        socio.setEstado(estadoBloqueado);
        socioRepository.save(socio);
    }

    // ===============================
    // ACTIVAR SOCIO
    // ===============================
    @Override
    @Transactional
    public void activarSocio(Long id) {
        SocioEntity socio = socioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + id));
        
        var estadoActivo = estadoRepository.findByEstado(EstadoSocioEnums.ACTIVO)
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));
        
        socio.setEstado(estadoActivo);
        socioRepository.save(socio);
    }

    // ===============================
    // MAPPER PRIVADO (BUENA PRÁCTICA)
    // ===============================
    private SocioDTO toDTO(SocioEntity s) {

        SocioDTO dto = new SocioDTO(
                s.getId(),
                s.getUsuario().getNombre(),
                s.getUsuario().getApellido(),
                s.getUsuario().getEmail(),
                s.getEstado().getEstado(),
                s.getCantidadInvitaciones(),
                s.getNumPostulaciones(),
                s.getUltimoMovimiento(),
                null
        );
        String email = s.getUsuario().getEmail();
        solicitudRepository.findTopByEmailOrderByFechaSolicitudDesc(email)
                .map(sol -> sol.getVehiculo())
                .filter(v -> v != null)
                .ifPresent(v -> dto.setPatente(v.getPatente()));
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