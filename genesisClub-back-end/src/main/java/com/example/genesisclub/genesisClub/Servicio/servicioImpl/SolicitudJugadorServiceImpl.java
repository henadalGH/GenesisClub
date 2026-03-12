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
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudJugadorDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.VehiculoEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.TipoSolicitud;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Repositorio.VehiculoRepository;
import com.example.genesisclub.genesisClub.Servicio.RegistroUsuarioServicio;
import com.example.genesisclub.genesisClub.Servicio.SolicitudJugadorService;

@Service
@Transactional
public class SolicitudJugadorServiceImpl implements SolicitudJugadorService {

    @Autowired private EstadoSolicitudRepository estadoSolicitudRepository;
    @Autowired private SolicitudReposistory solicitudRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RegistroUsuarioServicio registroService;
    @Autowired private VehiculoRepository vehiculoRepository;

    @Override
    public ResponceDTO solicitarJugador(SolicitudJugadorDTO dto) {
        ResponceDTO response = new ResponceDTO();
        if (validarEmailExistente(dto.getEmail(), response)) return response;

        SolicitudEntity solicitud = new SolicitudEntity();
        mapearDatosBasicos(solicitud, dto);

        VehiculoEntity veh = procesarVehiculo(dto.getPatente(), dto.getMarca(), dto.getModelo(), dto.getAnio(), dto.getTieneGnc());
        if (veh != null) {
            solicitud.setVehiculo(veh);
        }

        solicitud.setTipoSolicitud(TipoSolicitud.JUGADOR);

        EstadoSolicitudEntity estado = estadoSolicitudRepository.findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow(() -> new RuntimeException("Estado de solicitud no configurado"));

        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);
        response.setMensage("Solicitud creada correctamente");
        return response;
    }

    @Override
    public List<SolicitudJugadorDTO> obtenerSolicitudesPendientesJugador() {
        EstadoSolicitudEntity pendiente = estadoSolicitudRepository.findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();
        return solicitudRepository.findByEstadoAndTipoSolicitud(pendiente, TipoSolicitud.JUGADOR).stream()
                .map(this::mapToJugadorDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResponceDTO actualizarEstadoSolicitudJugador(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {
        ResponceDTO response = new ResponceDTO();
        SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getTipoSolicitud() != TipoSolicitud.JUGADOR) {
            response.setNumOfErrors(1);
            response.setMensage("Esta solicitud no es de tipo jugador");
            return response;
        }

        EstadoSolicitudEntity estado = estadoSolicitudRepository.findByEstado(nuevoEstado)
                .orElseThrow();

        solicitud.setEstado(estado);
        solicitud.setFechaSolicitud(LocalDate.now());

        if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {
            // Registrar al nuevo jugador
            RegistroDTO registroDTO = new RegistroDTO();
            registroDTO.setNombre(solicitud.getNombre());
            registroDTO.setApellido(solicitud.getApellido());
            registroDTO.setEmail(solicitud.getEmail());
            registroDTO.setPassword(solicitud.getPassword());
            registroDTO.setRol(RolesEnums.JUGADOR);

            registroService.registrarDesdeSolicitud(registroDTO);
        }

        solicitudRepository.save(solicitud);
        response.setMensage("Estado de la solicitud de jugador actualizado a: " + nuevoEstado);
        return response;
    }

    // =================== helpers ===================

    private void mapearDatosBasicos(SolicitudEntity entidad, SolicitudJugadorDTO dto) {
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

    private SolicitudJugadorDTO mapToJugadorDTO(SolicitudEntity s) {
        SolicitudJugadorDTO dto = new SolicitudJugadorDTO();
        dto.setId(s.getId());
        dto.setNombre(s.getNombre());
        dto.setApellido(s.getApellido());
        dto.setEmail(s.getEmail());
        dto.setContacto(s.getContacto());
        dto.setFechaSolicitud(s.getFechaSolicitud());
        dto.setEstado(s.getEstado().getEstado());
        if (s.getVehiculo() != null) {
            dto.setPatente(s.getVehiculo().getPatente());
            dto.setMarca(s.getVehiculo().getMarca());
            dto.setModelo(s.getVehiculo().getModelo());
            dto.setAnio(s.getVehiculo().getAnio());
            dto.setTieneGnc(s.getVehiculo().isTieneGnc());
        }
        return dto;
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
}