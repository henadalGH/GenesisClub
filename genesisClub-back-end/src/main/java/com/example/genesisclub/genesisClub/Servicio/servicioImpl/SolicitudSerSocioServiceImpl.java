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
import com.example.genesisclub.genesisClub.Modelo.Entidad.RelacionUsuarioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RelacionusuarioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.InvitacionRepository;
import com.example.genesisclub.genesisClub.Repositorio.RelacionUsuarioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
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
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RegistroUsuarioServicio registroService;
    @Autowired private InvitacionRepository invitacionRepository;
    @Autowired private SocioRepository socioRepository;
    @Autowired private RelacionUsuarioRepository relacionUsuarioRepository;

    @Override
    public ResponceDTO crearSolicitud(SolicitudDTO solicitudDTO, EstadoSolicitudEnums estadoSolicitud) {

        ResponceDTO response = new ResponceDTO();

        if (validarEmailExistente(solicitudDTO.getEmail(), response)) return response;

        SolicitudEntity solicitud = new SolicitudEntity();

        solicitud.setNombre(solicitudDTO.getNombre());
        solicitud.setApellido(solicitudDTO.getApellido());
        solicitud.setEmail(solicitudDTO.getEmail());
        solicitud.setContacto(solicitudDTO.getContacto());
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setPassword(passwordEncoder.encode(solicitudDTO.getPassword()));

        EstadoSolicitudEnums estadoEnum = estadoSolicitud != null ? estadoSolicitud : EstadoSolicitudEnums.PENDIENTE;

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(estadoEnum)
                .orElseThrow(() -> new RuntimeException("Estado no configurado"));

        solicitud.setEstado(estado);

        solicitudRepository.save(solicitud);

        response.setMensage("Solicitud creada correctamente");

        return response;
    }

    @Override
    public ResponceDTO crearSolicitudDesdeInvitacion(SolicitudDTO solicitudDTO, String token) {

        ResponceDTO response = new ResponceDTO();

        InvitacionEntity invitacion = invitacionRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {

            response.setNumOfErrors(1);
            response.setMensage("La invitación expiró");

            return response;
        }

        if (validarEmailExistente(solicitudDTO.getEmail(), response)) return response;

        SolicitudEntity solicitud = new SolicitudEntity();

        solicitud.setNombre(solicitudDTO.getNombre());
        solicitud.setApellido(solicitudDTO.getApellido());
        solicitud.setEmail(solicitudDTO.getEmail());
        solicitud.setContacto(solicitudDTO.getContacto());
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setPassword(passwordEncoder.encode(solicitudDTO.getPassword()));

        solicitud.setInvitacion(invitacion);
        solicitud.setSocio(invitacion.getSocioOrigen());

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();

        solicitud.setEstado(estado);

        solicitudRepository.save(solicitud);

        response.setMensage("Solicitud creada mediante invitación");

        return response;
    }

    @Override
    public ResponceDTO actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {

        ResponceDTO response = new ResponceDTO();

        SolicitudEntity solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(nuevoEstado)
                .orElseThrow();

        solicitud.setEstado(estado);

        if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {

            if (solicitud.getSocio() != null) {

                SocioEntity socioReferente = solicitud.getSocio();

                Integer actuales = socioReferente.getCantidadInvitaciones();

                socioReferente.setCantidadInvitaciones((actuales == null ? 0 : actuales) + 1);

                socioRepository.save(socioReferente);

                if (solicitud.getInvitacion() != null) {

                    InvitacionEntity inv = solicitud.getInvitacion();

                    inv.setFechaRespuesta(LocalDateTime.now());

                    invitacionRepository.save(inv);
                }
            }

            RegistroDTO registroDTO = new RegistroDTO();

            registroDTO.setNombre(solicitud.getNombre());
            registroDTO.setApellido(solicitud.getApellido());
            registroDTO.setEmail(solicitud.getEmail());
            registroDTO.setPassword(solicitud.getPassword());
            registroDTO.setRol(RolesEnums.SOCIO);
            registroDTO.setEstado(EstadoSocioEnums.ACTIVO);

            registroService.registrarDesdeSolicitud(registroDTO);

            SocioEntity nuevoSocio = socioRepository
                    .findByUsuarioEmail(solicitud.getEmail())
                    .orElseThrow(() -> new RuntimeException("Socio no encontrado"));

            if (solicitud.getSocio() != null) {

                if (!relacionUsuarioRepository.existsBySocioAndContacto(solicitud.getSocio(), nuevoSocio)) {

                    RelacionUsuarioEntity relacion = new RelacionUsuarioEntity();

                    relacion.setFecha(LocalDate.now());
                    relacion.setTipoRelacion(RelacionusuarioEnums.INVITADO);

                    relacion.setSocio(solicitud.getSocio());
                    relacion.setContacto(nuevoSocio);

                    relacionUsuarioRepository.save(relacion);
                }
            }
        }

        solicitudRepository.save(solicitud);

        response.setMensage("Estado actualizado");

        return response;
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPendientes() {

        EstadoSolicitudEntity pendiente = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();

        return solicitudRepository
                .findByEstado(pendiente)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private boolean validarEmailExistente(String email, ResponceDTO response) {

        boolean existeUsuario = usuarioRepository.existsByEmail(email);
        boolean existeSolicitud = solicitudRepository.existsByEmail(email);

        if (existeUsuario || existeSolicitud) {

            response.setNumOfErrors(1);
            response.setMensage("Email ya registrado");

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