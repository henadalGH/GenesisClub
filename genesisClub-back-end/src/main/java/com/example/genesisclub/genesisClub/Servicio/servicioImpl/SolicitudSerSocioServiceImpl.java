package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.example.genesisclub.genesisClub.Modelo.Entidad.InvitacionEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.InvitacionRepository;
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
    
    // Repositorio necesario para validar el token de invitación
    @Autowired private InvitacionRepository invitacionRepository;

    // =========================================
    // CREAR SOLICITUD (REGISTRO NORMAL)
    // =========================================
    @Override
    public ResponceDTO crearSolicitud(SolicitudDTO solicitudDTO, EstadoSolicitudEnums estadoSolicitud) {
        ResponceDTO response = new ResponceDTO();

        if (validarEmailExistente(solicitudDTO.getEmail(), response)) {
            return response;
        }

        SolicitudEntity solicitud = new SolicitudEntity();
        mapearDatosBasicos(solicitud, solicitudDTO);

        EstadoSolicitudEnums estadoEnum = 
                estadoSolicitud != null ? estadoSolicitud : EstadoSolicitudEnums.PENDIENTE;

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(estadoEnum)
                .orElseThrow(() -> new RuntimeException("Estado de solicitud no configurado"));

        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);

        response.setMensage("Solicitud creada correctamente");
        return response;
    }

    // =========================================
    // CREAR SOLICITUD DESDE INVITACIÓN (NUEVO)
    // =========================================
    @Override
    public ResponceDTO crearSolicitudDesdeInvitacion(SolicitudDTO solicitudDTO, String token) {
        ResponceDTO response = new ResponceDTO();

        // 1. Validar Token
        InvitacionEntity invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("El token de invitación no existe o es inválido."));

        // 2. Validar Expiración
        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            response.setNumOfErrors(1);
            response.setMensage("La invitación ha expirado. Solicita una nueva a tu socio referente.");
            return response;
        }

        // 3. Validar Email
        if (validarEmailExistente(solicitudDTO.getEmail(), response)) {
            return response;
        }

        // 4. Crear Solicitud vinculada
        SolicitudEntity solicitud = new SolicitudEntity();
        mapearDatosBasicos(solicitud, solicitudDTO);
        
        // RELACIÓN CLAVE: Vinculamos la solicitud con la invitación y el socio que invitó
        solicitud.setInvitacion(invitacion);
        solicitud.setSocio(invitacion.getSocioOrigen());

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();

        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);

        response.setMensage("Registro exitoso mediante invitación. Pendiente de aprobación por administración.");
        return response;
    }

    // =========================================
    // ACTUALIZAR ESTADO (APROBAR / RECHAZAR)
    // =========================================
    @Override
    public ResponceDTO actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {
        SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(nuevoEstado)
                .orElseThrow();

        solicitud.setEstado(estado);
        solicitud.setFechaModificacion(LocalDate.now());

        if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {
            RegistroDTO registroDTO = new RegistroDTO();
            registroDTO.setNombre(solicitud.getNombre());
            registroDTO.setApellido(solicitud.getApellido());
            registroDTO.setEmail(solicitud.getEmail());
            registroDTO.setPassword(solicitud.getPassword()); // Ya está encriptada
            registroDTO.setRol(RolesEnums.SOCIO);
            registroDTO.setEstado(EstadoSocioEnums.ACTIVO);

            registroService.registrarDesdeSolicitud(registroDTO);
        }

        solicitudRepository.save(solicitud);

        ResponceDTO response = new ResponceDTO();
        response.setMensage("Estado de la solicitud actualizado a: " + nuevoEstado);
        return response;
    }

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
    // MÉTODOS DE APOYO (HELPERS)
    // =========================================

    private void mapearDatosBasicos(SolicitudEntity entidad, SolicitudDTO dto) {
        entidad.setNombre(dto.getNombre());
        entidad.setApellido(dto.getApellido());
        entidad.setEmail(dto.getEmail());
        entidad.setContacto(dto.getContacto());
        entidad.setFechaSolicitud(LocalDate.now());
        entidad.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    private boolean validarEmailExistente(String email, ResponceDTO response) {
        boolean existeUsuario = usuarioRepository.existsByEmail(email);
        boolean existeSolicitud = solicitudRepository.existsByEmail(email);

        if (existeUsuario || existeSolicitud) {
            response.setNumOfErrors(1);
            response.setMensage("El correo electrónico ya está registrado o tiene una solicitud en curso.");
            return true;
        }
        return false;
    }

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