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
import com.example.genesisclub.genesisClub.Servicio.EmailService;
import com.example.genesisclub.genesisClub.Servicio.RegistroUsuarioServicio;
import com.example.genesisclub.genesisClub.Servicio.SolicitudSerSocioService;

@Service
public class SolicitudSerSocioServiceImpl implements SolicitudSerSocioService {

    @Autowired
    private EstadoSolicitudRepository estadoSolicitudRepository;

    @Autowired
    private SolicitudReposistory solicitudRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RegistroUsuarioServicio registroService; // Servicio que registra usuarios

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ------------------ Crear solicitud ------------------
    @Override
public ResponceDTO crearSolicitud(SolicitudDTO solicitudDTO, EstadoSolicitudEnums estadoSolicitud) {
    ResponceDTO response = new ResponceDTO();

    // 1️⃣ VALIDACIÓN DE SEGURIDAD (Doble chequeo)
    // Usamos el mismo mensaje que en 'registrarUsuario' para proteger la privacidad
    boolean existeComoUsuario = usuarioRepository.existsByEmail(solicitudDTO.getEmail());
    boolean existeComoSolicitud = solicitudRepository.existsByEmail(solicitudDTO.getEmail());

    if (existeComoUsuario || existeComoSolicitud) {
        response.setNumOfErrors(1);
        response.setMensage("El correo electrónico ya está vinculado a una cuenta o tiene una solicitud pendiente.");
        return response;
    }

    try {
        // 2️⃣ CREAR ENTIDAD SOLICITUD
        SolicitudEntity solicitudN = new SolicitudEntity();
        solicitudN.setNombre(solicitudDTO.getNombre());
        solicitudN.setApellido(solicitudDTO.getApellido());
        solicitudN.setEmail(solicitudDTO.getEmail());
        solicitudN.setContacto(solicitudDTO.getContacto());
        solicitudN.setFechaSolicitud(LocalDate.now());

        // 3️⃣ ENCRIPTAR CONTRASEÑA
        // Aquí no hace falta validar si ya está encriptada porque este método 
        // siempre recibe texto plano desde el formulario web de registro.
        solicitudN.setPassword(passwordEncoder.encode(solicitudDTO.getPassword()));

        // 4️⃣ ASIGNAR ESTADO
        EstadoSolicitudEnums estadoEnum = (estadoSolicitud != null) ? estadoSolicitud : EstadoSolicitudEnums.PENDIENTE;
        EstadoSolicitudEntity estadoEntity = estadoSolicitudRepository
                .findByEstado(estadoEnum)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + estadoEnum));

        solicitudN.setEstado(estadoEntity);
        
        // 5️⃣ GUARDAR
        solicitudRepository.save(solicitudN);

        response.setNumOfErrors(0);
        response.setMensage("Solicitud creada correctamente");

    } catch (RuntimeException e) {
        response.setNumOfErrors(1);
        response.setMensage("Error al procesar la solicitud: " + e.getMessage());
    }

    return response;
}

    // ------------------ Listar solicitudes pendientes ------------------
    @Override
    public List<SolicitudDTO> obtenerSolicitudesPendientes() {
        EstadoSolicitudEntity pendiente = estadoSolicitudRepository
                .findByEstado(EstadoSolicitudEnums.PENDIENTE)
                .orElseThrow(() -> new RuntimeException("Estado pendiente no encontrado"));

        return solicitudRepository.findByEstado(pendiente)
                .stream()
                .map(s -> {
                    SolicitudDTO dto = new SolicitudDTO();
                    dto.setId(s.getId());
                    dto.setNombre(s.getNombre());
                    dto.setApellido(s.getApellido());
                    dto.setEmail(s.getEmail());
                    dto.setContacto(s.getContacto());
                    dto.setFechaSolicitud(s.getFechaSolicitud());
                    dto.setEstado(s.getEstado().getEstado());
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
            SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + solicitudId));

            EstadoSolicitudEntity estadoEntity = estadoSolicitudRepository
                    .findByEstado(nuevoEstado)
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + nuevoEstado));

            solicitud.setEstado(estadoEntity);
            solicitudRepository.save(solicitud);

            // ------------------ Crear usuario si se aprueba ------------------
            if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {
                RegistroDTO registroDTO = new RegistroDTO();
                registroDTO.setNombre(solicitud.getNombre());
                registroDTO.setApellido(solicitud.getApellido());
                registroDTO.setEmail(solicitud.getEmail());
                registroDTO.setPassword(solicitud.getPassword()); // password enviado en la solicitud

                registroService.registrarUsuario(registroDTO, RolesEnums.SOCIO, EstadoSocioEnums.ACTIVO);
            }

            // ------------------ Enviar email de notificación ------------------
            String asunto = "";
            String cuerpo = "";

            if (nuevoEstado == EstadoSolicitudEnums.ACEPTADA) {
                asunto = "Solicitud aprobada";
                cuerpo = "¡Felicidades! Tu solicitud fue aprobada. Ya podés ingresar con tu usuario.";
            } else if (nuevoEstado == EstadoSolicitudEnums.RECHAZADA) {
                asunto = "Solicitud rechazada";
                cuerpo = "Tu solicitud fue rechazada. Por favor contactanos si necesitás más información.";
            }

            emailService.enviarEmail(solicitud.getEmail(), asunto, cuerpo);

            // ------------------ Devolver DTO actualizado ------------------
            SolicitudDTO dto = new SolicitudDTO();
            dto.setId(solicitud.getId());
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
        } catch (Exception e) {
            response.setNumOfErrors(1);
            response.setMensage("Error al crear usuario o enviar email: " + e.getMessage());
        }

        return response;
    }
}
