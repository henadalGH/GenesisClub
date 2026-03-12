package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.SocioDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Servicio.SocioServicio;

@Service
@Transactional(readOnly = true) // mantiene sesión abierta para Hibernate
public class ServicioSocioImpl implements SocioServicio {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private SolicitudReposistory solicitudRepository; // para obtener patente de la última solicitud

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
                null // la patente se asigna más abajo si existe
        );
        // obtener patente de la última solicitud asociada al email
        String email = s.getUsuario().getEmail();
        solicitudRepository.findTopByEmailOrderByFechaSolicitudDesc(email)
                .map(sol -> sol.getVehiculo())
                .filter(v -> v != null)
                .ifPresent(v -> dto.setPatente(v.getPatente()));
        return dto;
    }
}