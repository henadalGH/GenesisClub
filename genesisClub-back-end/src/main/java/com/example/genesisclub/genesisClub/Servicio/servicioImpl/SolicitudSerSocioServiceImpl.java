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
import com.example.genesisclub.genesisClub.Modelo.Entidad.VehiculoEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.TipoSolicitud;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSolicitudRepository;
import com.example.genesisclub.genesisClub.Repositorio.InvitacionRepository;
import com.example.genesisclub.genesisClub.Repositorio.RelacionUsuarioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Repositorio.VehiculoRepository;
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
    @Autowired private RelacionUsuarioRepository relacionRepository;
    @Autowired private VehiculoRepository vehiculoRepository;

    @Override
    public ResponceDTO crearSolicitud(SolicitudDTO solicitudDTO, EstadoSolicitudEnums estadoSolicitud) {

        ResponceDTO response = new ResponceDTO();

        if (validarEmailExistente(solicitudDTO.getEmail(), response)) {
            return response;
        }

        if (!validarPatenteSegunAnio(solicitudDTO.getPatente(), solicitudDTO.getAnio())) {
            response.setNumOfErrors(1);
            response.setMensage("La patente no coincide con el año del vehículo según normativa argentina.");
            return response;
        }

        if (solicitudDTO.getPatente() != null &&
            vehiculoRepository.existsByPatente(solicitudDTO.getPatente().trim().toUpperCase())) {

            response.setNumOfErrors(1);
            response.setMensage("La patente ya se encuentra registrada.");
            return response;
        }

        if (!validarAnioVehiculo(solicitudDTO.getAnio(), response)) {
            return response;
        }

        SolicitudEntity solicitud = new SolicitudEntity();
        mapearDatosBasicos(solicitud, solicitudDTO);
        solicitud.setTipoSolicitud(TipoSolicitud.SOCIO);

        if (solicitudDTO.getPatente() != null) {
            VehiculoEntity veh = procesarVehiculo(
                    solicitudDTO.getPatente(),
                    solicitudDTO.getMarca(),
                    solicitudDTO.getModelo(),
                    solicitudDTO.getAnio(),
                    solicitudDTO.getTieneGnc()
            );
            solicitud.setVehiculo(veh);
        }

        EstadoSolicitudEnums estadoEnum =
                estadoSolicitud != null ? estadoSolicitud : EstadoSolicitudEnums.PENDIENTE;

        EstadoSolicitudEntity estado = estadoSolicitudRepository.findByEstado(estadoEnum)
                .orElseThrow(() -> new RuntimeException("Estado no configurado"));

        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);

        response.setMensage("Solicitud creada correctamente");

        return response;
    }

    @Override
    public ResponceDTO crearSolicitudDesdeInvitacion(SolicitudDTO solicitudDTO, String token) {

        ResponceDTO response = new ResponceDTO();

        InvitacionEntity invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            response.setNumOfErrors(1);
            response.setMensage("La invitación expiró.");
            return response;
        }

        if (validarEmailExistente(solicitudDTO.getEmail(), response)) {
            return response;
        }

        SolicitudEntity solicitud = new SolicitudEntity();
        mapearDatosBasicos(solicitud, solicitudDTO);
        solicitud.setTipoSolicitud(TipoSolicitud.SOCIO);

        if (solicitudDTO.getPatente() != null) {
            VehiculoEntity veh = procesarVehiculo(
                    solicitudDTO.getPatente(),
                    solicitudDTO.getMarca(),
                    solicitudDTO.getModelo(),
                    solicitudDTO.getAnio(),
                    solicitudDTO.getTieneGnc()
            );
            solicitud.setVehiculo(veh);
        }

        solicitud.setInvitacion(invitacion);
        solicitud.setSocio(invitacion.getSocioOrigen());

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow();

        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);

        response.setMensage("Solicitud creada mediante invitación.");

        return response;
    }

    @Override
public ResponceDTO actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {

    ResponceDTO response = new ResponceDTO();

    // Obtener la solicitud
    SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

    // Obtener el nuevo estado
    EstadoSolicitudEntity estado = estadoSolicitudRepository
            .findByEstado(nuevoEstado)
            .orElseThrow();

    solicitud.setEstado(estado);
    solicitud.setFechaSolicitud(LocalDate.now());

    // Si la solicitud es aceptada
    if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {

        // Determinar el rol a asignar
        RolesEnums rolAsignado =
                solicitud.getTipoSolicitud() == TipoSolicitud.JUGADOR
                        ? RolesEnums.JUGADOR
                        : RolesEnums.SOCIO;

        // Preparar DTO de registro
        RegistroDTO registroDTO = new RegistroDTO();
        registroDTO.setNombre(solicitud.getNombre());
        registroDTO.setApellido(solicitud.getApellido());
        registroDTO.setEmail(solicitud.getEmail());
        registroDTO.setPassword(solicitud.getPassword());
        registroDTO.setRol(rolAsignado);
        registroDTO.setEstado(EstadoSocioEnums.ACTIVO);

        // Registrar usuario/socio
        registroService.registrarDesdeSolicitud(registroDTO);

        // Obtener el socio recién creado
        SocioEntity nuevoSocio = socioRepository
                .findByUsuarioEmail(solicitud.getEmail())
                .orElseThrow();

        // Manejar relación con socio padre si aplica
        if (solicitud.getSocio() != null) {
            SocioEntity socioPadre = solicitud.getSocio();
            Integer actuales = socioPadre.getCantidadInvitaciones();
            socioPadre.setCantidadInvitaciones((actuales == null ? 0 : actuales) + 1);
            socioRepository.save(socioPadre);

            RelacionUsuarioEntity relacion = new RelacionUsuarioEntity();
            relacion.setSocioPadre(socioPadre);
            relacion.setSocioHijo(nuevoSocio);
            relacion.setNivel(1);
            relacion.setFecha(LocalDate.now());
            relacionRepository.save(relacion);
        }

        // 🔹 Asociar vehículo al usuario del nuevo socio
        if (solicitud.getVehiculo() != null) {
            VehiculoEntity vehiculo = solicitud.getVehiculo();
            vehiculo.setUsuario(nuevoSocio.getUsuario()); // usuario asociado al socio
            vehiculoRepository.save(vehiculo);
        }

        // Manejar invitación si existe
        if (solicitud.getInvitacion() != null) {
            InvitacionEntity inv = solicitud.getInvitacion();
            inv.setFechaRespuesta(LocalDateTime.now());
            invitacionRepository.save(inv);
        }
    }

    // Guardar cambios en la solicitud
    solicitudRepository.save(solicitud);

    response.setMensage("Estado actualizado a " + nuevoEstado);

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

    private void mapearDatosBasicos(SolicitudEntity entidad, SolicitudDTO dto) {

        entidad.setNombre(dto.getNombre().trim());
        entidad.setApellido(dto.getApellido().trim());
        entidad.setEmail(dto.getEmail().trim().toLowerCase());
        entidad.setCodigoArea(dto.getCodigoArea());
        entidad.setNumeroCelular(dto.getNumeroCelular());
        entidad.setFechaSolicitud(LocalDate.now());
        entidad.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    private VehiculoEntity procesarVehiculo(String patente, String marca,
                                            String modelo, Integer anio, Boolean tieneGnc) {

        if (patente == null) return null;

        String patenteNormalizada = patente.trim().toUpperCase();

        return vehiculoRepository.findByPatente(patenteNormalizada).orElseGet(() -> {

            VehiculoEntity v = new VehiculoEntity();

            v.setPatente(patenteNormalizada);
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
            response.setMensage("El email ya está registrado o tiene una solicitud en proceso.");

            return true;
        }

        return false;
    }

    private boolean validarPatenteSegunAnio(String patente, Integer anio) {

        if (patente == null || anio == null) return true;

        if (anio < 2016) {
            return patente.matches("^[A-Z]{3}[0-9]{3}$");
        } else {
            return patente.matches("^[A-Z]{2}[0-9]{3}[A-Z]{2}$");
        }
    }

    private boolean validarAnioVehiculo(Integer anio, ResponceDTO response) {

        if (anio == null) return true;

        int anioActual = LocalDate.now().getYear();

        if (anio < 1900 || anio > anioActual) {

            response.setNumOfErrors(1);
            response.setMensage("El año del vehículo no es válido.");

            return false;
        }

        return true;
    }

    private SolicitudDTO mapToDTO(SolicitudEntity s) {

        SolicitudDTO dto = new SolicitudDTO();

        dto.setId(s.getId());
        dto.setNombre(s.getNombre());
        dto.setApellido(s.getApellido());
        dto.setEmail(s.getEmail());
        dto.setCodigoArea(s.getCodigoArea());
        dto.setNumeroCelular(s.getNumeroCelular());
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