package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
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
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
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

    // =========================
    // REGEX PATENTE ARGENTINA
    // =========================
    private static final Pattern PATENTE_VIEJA = Pattern.compile("^[A-Z]{3}[0-9]{3}$"); // ABC123
    private static final Pattern PATENTE_NUEVA = Pattern.compile("^[A-Z]{2}[0-9]{3}[A-Z]{2}$"); // AB123CD

    @Override
    public ResponceDTO solicitarJugador(SolicitudJugadorDTO dto) {

        ResponceDTO response = new ResponceDTO();

        if (validarEmailExistente(dto.getEmail(), response)) return response;

        // validar patente
        if (!validarPatente(dto.getPatente(), response)) return response;

        // validar año según patente
        if (!validarAnioVehiculo(dto.getPatente(), dto.getAnio(), response)) return response;

        SolicitudEntity solicitud = new SolicitudEntity();

        mapearDatosBasicos(solicitud, dto);

        VehiculoEntity veh = procesarVehiculo(
                dto.getPatente(),
                dto.getMarca(),
                dto.getModelo(),
                dto.getAnio(), 
                dto.getTieneGnc()
        );

        if (veh != null) {
            solicitud.setVehiculo(veh);
        }

        solicitud.setTipoSolicitud(TipoSolicitud.JUGADOR);

        EstadoSolicitudEntity estado = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow(() -> new RuntimeException("Estado de solicitud no configurado"));

        solicitud.setEstado(estado);

        solicitudRepository.save(solicitud);

        response.setMensage("Solicitud creada correctamente");

        return response;
    }

    @Override
    public List<SolicitudJugadorDTO> obtenerSolicitudesPendientesJugador() {

        EstadoSolicitudEntity pendiente = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow(); 

        return solicitudRepository
                .findByEstadoAndTipoSolicitud(pendiente, TipoSolicitud.JUGADOR)
                .stream()
                .map(this::mapToJugadorDTO)
                .collect(Collectors.toList());
    }

    @Override
public ResponceDTO actualizarEstadoSolicitudJugador(Long solicitudId, EstadoSolicitudEnums nuevoEstado) {

    ResponceDTO response = new ResponceDTO();

    // Obtener solicitud
    SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

    if (solicitud.getTipoSolicitud() != TipoSolicitud.JUGADOR) {
        response.setNumOfErrors(1);
        response.setMensage("Esta solicitud no es de tipo jugador");
        return response;
    }

    // Obtener estado
    EstadoSolicitudEntity estado = estadoSolicitudRepository
            .findByEstado(nuevoEstado)
            .orElseThrow();

    solicitud.setEstado(estado);
    solicitud.setFechaSolicitud(LocalDate.now());

    // Si la solicitud es aceptada
    if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {

        RegistroDTO registroDTO = new RegistroDTO();
        registroDTO.setNombre(solicitud.getNombre());
        registroDTO.setApellido(solicitud.getApellido());
        registroDTO.setEmail(solicitud.getEmail());
        registroDTO.setPassword(solicitud.getPassword());
        registroDTO.setCodigoArea(solicitud.getCodigoArea());
        registroDTO.setNumeroCelular(solicitud.getNumeroCelular());
        registroDTO.setRol(RolesEnums.JUGADOR);

        // Crear usuario
        registroService.registrarDesdeSolicitud(registroDTO);

        // Obtener usuario creado
        UsuarioEntity usuario = usuarioRepository
                .findByEmail(solicitud.getEmail())
                .orElseThrow();

        // Asociar vehículo al usuario
        if (solicitud.getVehiculo() != null) {

            VehiculoEntity vehiculo = solicitud.getVehiculo();

            vehiculo.setUsuario(usuario);

            vehiculoRepository.save(vehiculo);
        }
    }

    solicitudRepository.save(solicitud);

    response.setMensage("Estado actualizado a: " + nuevoEstado);

    return response;
}

    // =========================
    // VALIDACIONES
    // =========================

    private boolean validarPatente(String patente, ResponceDTO response) {

        if (patente == null || patente.isEmpty()) {
            response.setNumOfErrors(1);
            response.setMensage("Debe ingresar una patente");
            return false;
        }

        boolean formatoValido =
                PATENTE_VIEJA.matcher(patente).matches() ||
                PATENTE_NUEVA.matcher(patente).matches();

        if (!formatoValido) {

            response.setNumOfErrors(1);
            response.setMensage("Formato de patente inválido");

            return false;
        }

        return true;
    }

    private boolean validarAnioVehiculo(String patente, Integer anio, ResponceDTO response) {

        if (anio == null) return true;

        if (PATENTE_VIEJA.matcher(patente).matches() && anio >= 2016) {

            response.setNumOfErrors(1);
            response.setMensage("Las patentes viejas son para vehículos anteriores a 2016");

            return false;
        }

        if (PATENTE_NUEVA.matcher(patente).matches() && anio < 2016) {

            response.setNumOfErrors(1);
            response.setMensage("Las patentes nuevas corresponden a vehículos desde 2016");

            return false;
        }

        return true;
    }

    // =========================
    // HELPERS
    // =========================

    private void mapearDatosBasicos(SolicitudEntity entidad, SolicitudJugadorDTO dto) {

        entidad.setNombre(dto.getNombre());
        entidad.setApellido(dto.getApellido());
        entidad.setEmail(dto.getEmail());
        entidad.setCodigoArea(dto.getCodigoArea());
        entidad.setNumeroCelular(dto.getNumeroCelular());
        entidad.setFechaSolicitud(dto.getFechaSolicitud());
        entidad.setFechaSolicitud(LocalDate.now());
        entidad.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    private VehiculoEntity procesarVehiculo(String patente, String marca,
            String modelo, Integer anio, Boolean tieneGnc) {

        if (patente == null) return null;

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
        dto.setCodigoArea(s.getCodigoArea());
        dto.setNumeroCelular(s.getNumeroCelular());
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
            response.setMensage("El correo ya está registrado o tiene una solicitud pendiente");

            return true;
        }

        return false;
    }
}