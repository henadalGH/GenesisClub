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
import com.example.genesisclub.genesisClub.Modelo.Entidad.VehiculoEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RelacionUsuarioEntity; // IMPORTANTE
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.TipoSolicitud;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.InvitacionRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Repositorio.VehiculoRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.RelacionUsuarioRepository; 
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
    @Autowired private RelacionUsuarioRepository relacionRepository; // Inyectado para el multinivel
    @Autowired private VehiculoRepository vehiculoRepository; // para datos de patente

    @Override
    public ResponceDTO crearSolicitud(SolicitudDTO solicitudDTO, EstadoSolicitudEnums estadoSolicitud) {
        ResponceDTO response = new ResponceDTO();
        if (validarEmailExistente(solicitudDTO.getEmail(), response)) return response;

        SolicitudEntity solicitud = new SolicitudEntity();
        mapearDatosBasicos(solicitud, solicitudDTO);
        solicitud.setTipoSolicitud(TipoSolicitud.SOCIO);

        // procesar vehículo si llegó patente
        if (solicitudDTO.getPatente() != null) {
            VehiculoEntity veh = procesarVehiculo(solicitudDTO.getPatente(), solicitudDTO.getMarca(),
                    solicitudDTO.getModelo(), solicitudDTO.getAnio(), solicitudDTO.getTieneGnc());
            if (veh != null) {
                solicitud.setVehiculo(veh);
            }
        }

        EstadoSolicitudEnums estadoEnum = estadoSolicitud != null ? estadoSolicitud : EstadoSolicitudEnums.PENDIENTE;
        EstadoSolicitudEntity estado = estadoSolicitudRepository.findByEstado(estadoEnum)
                .orElseThrow(() -> new RuntimeException("Estado de solicitud no configurado"));

        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);
        response.setMensage("Solicitud creada correctamente");
        return response;
    }

    @Override
    public ResponceDTO crearSolicitudDesdeInvitacion(SolicitudDTO solicitudDTO, String token) {
        ResponceDTO response = new ResponceDTO();

        InvitacionEntity invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("El token de invitación no existe o es inválido."));

        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            response.setNumOfErrors(1);
            response.setMensage("La invitación ha expirado. Solicita una nueva a tu socio referente.");
            return response;
        }

        if (validarEmailExistente(solicitudDTO.getEmail(), response)) return response;

        if (solicitudDTO.getPassword() == null || solicitudDTO.getPassword().isEmpty()) {
            response.setNumOfErrors(1);
            response.setMensage("Debe ingresar un password para completar el registro.");
            return response;
        }

        SolicitudEntity solicitud = new SolicitudEntity();
        mapearDatosBasicos(solicitud, solicitudDTO);
        solicitud.setTipoSolicitud(TipoSolicitud.SOCIO);

        // añadir datos de vehículo si existen
        if (solicitudDTO.getPatente() != null) {
            VehiculoEntity veh = procesarVehiculo(solicitudDTO.getPatente(), solicitudDTO.getMarca(),
                    solicitudDTO.getModelo(), solicitudDTO.getAnio(), solicitudDTO.getTieneGnc());
            if (veh != null) {
                solicitud.setVehiculo(veh);
            }
        }

        solicitud.setInvitacion(invitacion);
        solicitud.setSocio(invitacion.getSocioOrigen());

        EstadoSolicitudEntity estado = estadoSolicitudRepository.findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();
        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);

        response.setMensage("Registro exitoso mediante invitación. Pendiente de aprobación por administración.");
        return response;
    }

    @Override
    public ResponceDTO actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {
        ResponceDTO response = new ResponceDTO();
        SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        EstadoSolicitudEntity estado = estadoSolicitudRepository.findByEstado(nuevoEstado)
                .orElseThrow();

        solicitud.setEstado(estado);
        solicitud.setFechaSolicitud(LocalDate.now());

        if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {
            
            // Determinar el rol basado en el tipo de solicitud
            RolesEnums rolAsignado;
            if (solicitud.getTipoSolicitud() == TipoSolicitud.SOCIO) {
                rolAsignado = RolesEnums.SOCIO;
            } else if (solicitud.getTipoSolicitud() == TipoSolicitud.JUGADOR) {
                rolAsignado = RolesEnums.JUGADOR;
            } else {
                rolAsignado = RolesEnums.SOCIO; // default
            }

            // 1. REGISTRAR AL NUEVO USUARIO
            RegistroDTO registroDTO = new RegistroDTO();
            registroDTO.setNombre(solicitud.getNombre());
            registroDTO.setApellido(solicitud.getApellido());
            registroDTO.setEmail(solicitud.getEmail());
            registroDTO.setPassword(solicitud.getPassword());
            registroDTO.setRol(rolAsignado);
            registroDTO.setEstado(EstadoSocioEnums.ACTIVO);

            registroService.registrarDesdeSolicitud(registroDTO);

            // 2. LÓGICA MULTINIVEL: Solo para SOCIOS
            if (solicitud.getTipoSolicitud() == TipoSolicitud.SOCIO) {
                // Vincular con el socio referente
                if (solicitud.getSocio() != null) {
                    SocioEntity socioReferente = solicitud.getSocio();
                    
                    // Sumar invitación al contador del padre
                    Integer actuales = socioReferente.getCantidadInvitaciones();
                    socioReferente.setCantidadInvitaciones((actuales == null ? 0 : actuales) + 1);
                    socioRepository.save(socioReferente);
                    
                    // Buscar al nuevo socio recién creado para establecer la relación
                    SocioEntity nuevoSocioHijo = socioRepository.findByUsuarioEmail(solicitud.getEmail())
                            .orElseThrow(() -> new RuntimeException("Error al recuperar el nuevo socio creado"));

                    // Crear la fila en la tabla relacion_usuario
                    RelacionUsuarioEntity nuevaRelacion = new RelacionUsuarioEntity();
                    nuevaRelacion.setSocioPadre(socioReferente);
                    nuevaRelacion.setSocioHijo(nuevoSocioHijo);
                    nuevaRelacion.setNivel(1); // Nivel 1 por ser directo
                    nuevaRelacion.setFecha(LocalDate.now());
                    
                    relacionRepository.save(nuevaRelacion);
                }
                
                // Marcar invitación como respondida
                if (solicitud.getInvitacion() != null) {
                    InvitacionEntity inv = solicitud.getInvitacion();
                    inv.setFechaRespuesta(LocalDateTime.now());
                    invitacionRepository.save(inv);
                }
            }
        }

        solicitudRepository.save(solicitud);
        response.setMensage("Estado de la solicitud actualizado a: " + nuevoEstado);
        return response;
    }

    @Override
    public List<SolicitudDTO> obtenerSolicitudesPendientes() {
        EstadoSolicitudEntity pendiente = estadoSolicitudRepository.findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();
        return solicitudRepository.findByEstado(pendiente).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =================== Helpers ===================

    private void mapearDatosBasicos(SolicitudEntity entidad, SolicitudDTO dto) {
        entidad.setNombre(dto.getNombre());
        entidad.setApellido(dto.getApellido());
        entidad.setEmail(dto.getEmail());
        entidad.setContacto(dto.getContacto());
        entidad.setFechaSolicitud(LocalDate.now());
        entidad.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    private VehiculoEntity procesarVehiculo(String patente, String marca,
            String modelo, Integer anio, Boolean tieneGnc) {
        if (patente == null) {
            return null;
        }
        return vehiculoRepository.findByPatente(patente).orElseGet(() -> {
            VehiculoEntity v = new VehiculoEntity();
            v.setPatente(patente);
            v.setMarca(marca);
            v.setModelo(modelo);
            v.setAnio(anio);
            v.setTieneGnc(tieneGnc != null ? tieneGnc : false);
            v.setFechaRegistro(LocalDate.now());
            return vehiculoRepository.save(v);
        });
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
        dto.setTipoSolicitud(s.getTipoSolicitud());
        if (s.getVehiculo() != null) {
            dto.setPatente(s.getVehiculo().getPatente());
            dto.setMarca(s.getVehiculo().getMarca());
            dto.setModelo(s.getVehiculo().getModelo());
            dto.setAnio(s.getVehiculo().getAnio());
            dto.setTieneGnc(s.getVehiculo().isTieneGnc());
        }
        return dto;
    }

}