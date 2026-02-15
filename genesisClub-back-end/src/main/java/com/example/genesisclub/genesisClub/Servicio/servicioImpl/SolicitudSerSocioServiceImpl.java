package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Servicio.SolicitudSerSocioService;

@Service
public class SolicitudSerSocioServiceImpl implements SolicitudSerSocioService {

    @Autowired
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @Autowired
    private SolicitudReposistory solicitudReposistory;

    // ------------------ Crear solicitud ------------------
    @Override
    public ResponceDTO crearSolicitud(SolicitudDTO solicitud, EstadoSolicitudEnums estadoSolicitud) {
        ResponceDTO response = new ResponceDTO();

        try {
            SolicitudEntity solicitudN = new SolicitudEntity();
            solicitudN.setNombre(solicitud.getNombre());
            solicitudN.setApellido(solicitud.getApellido());
            solicitudN.setEmail(solicitud.getEmail());
            solicitudN.setContacto(solicitud.getContacto());
            solicitudN.setFechaSolicitud(LocalDate.now());

            EstadoSolicitudEnums estadoEnum = (estadoSolicitud != null) ? estadoSolicitud : EstadoSolicitudEnums.PENDIENTE;

            EstadoSolicitudEntity estadoEntity = estadoSolicitudRepository
                    .findByEstado(estadoEnum)
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + estadoEnum));

            solicitudN.setEstado(estadoEntity);

            solicitudReposistory.save(solicitudN);

            response.setNumOfErrors(0);
            response.setMensage("Solicitud creada correctamente");

        } catch (RuntimeException e) {
            response.setNumOfErrors(1);
            response.setMensage("Error al crear solicitud: " + e.getMessage());
        }

        return response;
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPendientes() {
        EstadoSolicitudEntity pendiente = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow(() -> new RuntimeException("Estado pendiente no encontrado"));

        return solicitudReposistory.findByEstado(pendiente)
                .stream()
                .map(s -> {
                    SolicitudDTO dto = new SolicitudDTO();
                    dto.setNombre(s.getNombre());
                    dto.setApellido(s.getApellido());
                    dto.setEmail(s.getEmail());
                    dto.setContacto(s.getContacto());
                    dto.setFechaSolicitud(s.getFechaSolicitud());
                    dto.setEstado(s.getEstado().getEstado()); // Convierte la entidad a enum
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ------------------ Actualizar estado de solicitud ------------------
    @Transactional
    @Override
    public ResponceDTO actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {
        ResponceDTO response = new ResponceDTO();

        try {
            SolicitudEntity solicitud = solicitudReposistory.findById(solicitudId)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + solicitudId));

            EstadoSolicitudEntity estadoEntity = estadoSolicitudRepository
                    .findByEstado(nuevoEstado)
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + nuevoEstado));

            solicitud.setEstado(estadoEntity);
            solicitudReposistory.save(solicitud);

            // Devolver la solicitud actualizada en DTO
            SolicitudDTO dto = new SolicitudDTO();
            dto.setNombre(solicitud.getNombre());
            dto.setApellido(solicitud.getApellido());
            dto.setEmail(solicitud.getEmail());
            dto.setContacto(solicitud.getContacto());
            dto.setFechaSolicitud(solicitud.getFechaSolicitud());
            dto.setEstado(solicitud.getEstado().getEstado());

            response.setNumOfErrors(0);
            response.setMensage("Estado de la solicitud actualizado a " + nuevoEstado);
            

        } catch (RuntimeException e) {
            response.setNumOfErrors(1);
            response.setMensage("Error al actualizar estado: " + e.getMessage());
        }

        return response;
    }
}
