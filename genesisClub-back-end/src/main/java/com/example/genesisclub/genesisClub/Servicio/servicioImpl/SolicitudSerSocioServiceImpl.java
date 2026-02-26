package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.RegistroUsuarioServicio;
import com.example.genesisclub.genesisClub.Servicio.SolicitudSerSocioService;

@Service
@Transactional
public class SolicitudSerSocioServiceImpl implements SolicitudSerSocioService {

    @Autowired private EstadoSolicitudRepository estadoSolicitudRepository;
    @Autowired private SolicitudReposistory solicitudRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RegistroUsuarioServicio registroService;
    @Autowired private UsuarioRepository usuarioRepository;

    // =========================================
    // CREAR SOLICITUD
    // =========================================
    @Override
    public ResponceDTO crearSolicitud(SolicitudDTO solicitudDTO, EstadoSolicitudEnums estadoSolicitud) {

        ResponceDTO response = new ResponceDTO();

        boolean existeUsuario = usuarioRepository.existsByEmail(solicitudDTO.getEmail());
        boolean existeSolicitud = solicitudRepository.existsByEmail(solicitudDTO.getEmail());

        if (existeUsuario || existeSolicitud) {
            response.setNumOfErrors(1);
            response.setMensage("El correo electrónico ya está vinculado a una cuenta o tiene una solicitud pendiente.");
            return response;
        }

        SolicitudEntity solicitud = new SolicitudEntity();

        solicitud.setNombre(solicitudDTO.getNombre());
        solicitud.setApellido(solicitudDTO.getApellido());
        solicitud.setEmail(solicitudDTO.getEmail());
        solicitud.setContacto(solicitudDTO.getContacto());
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setPassword(passwordEncoder.encode(solicitudDTO.getPassword()));

        EstadoSolicitudEnums estadoEnum =
                estadoSolicitud != null ? estadoSolicitud : EstadoSolicitudEnums.PENDIENTE;

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(estadoEnum)
                .orElseThrow();

        solicitud.setEstado(estado);

        solicitudRepository.save(solicitud);

        response.setMensage("Solicitud creada correctamente");
        return response;
    }

    // =========================================
    // LISTAR PENDIENTES
    // =========================================
    @Override
    public List<SolicitudDTO> obtenerSolicitudesPendientes() {

        EstadoSolicitudEntity pendiente = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();

        return solicitudRepository.findByEstado(pendiente)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =========================================
    // APROBAR / RECHAZAR
    // =========================================
    @Override
    public ResponceDTO actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {

        SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(nuevoEstado)
                .orElseThrow();

        solicitud.setEstado(estado);

        // =====================================
        // SI ACEPTADA → CREAR SOCIO
        // =====================================
        if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {

            RegistroDTO registroDTO = new RegistroDTO();
            registroDTO.setNombre(solicitud.getNombre());
            registroDTO.setApellido(solicitud.getApellido());
            registroDTO.setEmail(solicitud.getEmail());

            // ⚠ password YA encriptada → no re-encode
            registroDTO.setPassword(solicitud.getPassword());

            registroDTO.setRol(RolesEnums.SOCIO);
            registroDTO.setEstado(EstadoSocioEnums.ACTIVO);

            // 👉 USAR MÉTODO ESPECIAL QUE IGNORA SOLICITUD
            registroService.registrarDesdeSolicitud(registroDTO);
        }

        solicitudRepository.save(solicitud);

        ResponceDTO response = new ResponceDTO();
        response.setMensage("Estado actualizado correctamente");

        return response;
    }

    // =========================================
    // MAPPER
    // =========================================
    private SolicitudDTO mapToDTO(SolicitudEntity s) {

        SolicitudDTO dto = new SolicitudDTO();

        dto.setId(s.getId());
        dto.setNombre(s.getNombre());
        dto.setApellido(s.getApellido());
        dto.setEmail(s.getEmail());
        dto.setContacto(s.getContacto());
        dto.setFechaSolicitud(s.getFechaSolicitud());
        dto.setEstado(s.getEstado().getEstado());

        return dto;
    }
}