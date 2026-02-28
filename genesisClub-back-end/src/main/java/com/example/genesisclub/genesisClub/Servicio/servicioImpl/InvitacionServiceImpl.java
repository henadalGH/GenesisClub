package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.Responce.InvitacionResponseDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoInvitacionEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.InvitacionEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoinvitacionEnums;
import com.example.genesisclub.genesisClub.Repositorio.EstadoInvitacionRepository;
import com.example.genesisclub.genesisClub.Repositorio.InvitacionRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Servicio.EmailService;
import com.example.genesisclub.genesisClub.Servicio.InvitacionService;

import jakarta.mail.MessagingException;


@Service
public class InvitacionServiceImpl implements InvitacionService {

    @Autowired
    private InvitacionRepository invitacionRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private EstadoInvitacionRepository estadoRepository;

    @Autowired
    private EmailService emailService;

    @Override
public InvitacionResponseDTO crearInvitacion(Long socioId, InvitacionRequestDTO dto) throws MessagingException {

    // 1️⃣ validar socio origen
    SocioEntity socioOrigen = socioRepository.findById(socioId)
            .orElseThrow(() -> new RuntimeException("Socio no encontrado"));

    // 2️⃣ buscar estado PENDIENTE
    EstadoInvitacionEntity estado = estadoRepository
            .findByEstado(EstadoinvitacionEnums.PENDIENTE)
            .orElseThrow(() -> new RuntimeException("Estado no configurado"));

    // 3️⃣ generar token único
    String token = UUID.randomUUID().toString();

    // 4️⃣ crear entidad
    InvitacionEntity invitacion = new InvitacionEntity();
    invitacion.setSocioOrigen(socioOrigen);
    invitacion.setEmailDestino(dto.getEmailDestino());
    invitacion.setToken(token);
    invitacion.setFechaExpiracion(LocalDateTime.now().plusDays(2));
    invitacion.setEstado(estado);

    invitacionRepository.save(invitacion);

    // 🚀 CORRECCIÓN AQUÍ: Accedemos al nombre a través de la relación con Usuario
    // Asegúrate de que los nombres de los métodos coincidan con tus entidades
    String nombreUsuario = socioOrigen.getUsuario().getNombre(); 
    enviarEmailInvitacion(invitacion, nombreUsuario);

    // 5️⃣ mapear respuesta
    InvitacionResponseDTO response = new InvitacionResponseDTO();
    response.setId(invitacion.getId());
    response.setEmailDestino(invitacion.getEmailDestino());
    response.setToken(invitacion.getToken());
    response.setEstado(estado.getEstado().name());

    return response;
}

    // Método privado para no ensuciar la lógica principal
    
    private void enviarEmailInvitacion(InvitacionEntity invitacion, String nombreSocio) throws MessagingException {
        // Ajusta esta URL a la ruta de tu Front-end o Controller de registro
        String urlRegistro = "http://localhost:8080/api/usuario/registro?token=" + invitacion.getToken();

        com.example.genesisclub.genesisClub.Modelo.DTO.EmailDTO emailDTO = new com.example.genesisclub.genesisClub.Modelo.DTO.EmailDTO();
        emailDTO.setDestinatario(invitacion.getEmailDestino());
        emailDTO.setAsunto("¡Has sido invitado a GenesisClub!");
        
        // El mensaje que se inyectará en th:text="${mensaje}" de tu mail.html
        emailDTO.setMensaje("Hola, " + nombreSocio + " te ha invitado a formar parte de nuestra comunidad. " +
                        "Para completar tu registro, haz clic en el siguiente enlace: " + urlRegistro);

        emailService.enviarCorreo(emailDTO);
    }

    @Override
        public InvitacionResponseDTO aceptarInvitacion(String token) {

            InvitacionEntity invitacion = invitacionRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invitación no válida"));

            // 1. Validar expiración
            if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("La invitación expiró");
            }

            // 2. Validar estado (solo se puede aceptar si está PENDIENTE)
            if (!invitacion.getEstado().getEstado().equals(EstadoinvitacionEnums.PENDIENTE)) {
                throw new RuntimeException("La invitación ya fue usada o cancelada");
            }

            // 3. Buscar estado ACEPTADA
            EstadoInvitacionEntity estadoAceptado = estadoRepository
                    .findByEstado(EstadoinvitacionEnums.ACEPTADA)
                    .orElseThrow(() -> new RuntimeException("Estado ACEPTADA no configurado"));

            // 🚀 ACTUALIZACIÓN PARA EL ADMIN:
            invitacion.setEstado(estadoAceptado);
            invitacion.setFechaRespuesta(LocalDateTime.now()); // <--- Guardamos el momento exacto

            invitacionRepository.save(invitacion);

            // 4. Mapear respuesta
            InvitacionResponseDTO response = new InvitacionResponseDTO();
            response.setId(invitacion.getId());
            response.setEmailDestino(invitacion.getEmailDestino());
            response.setToken(invitacion.getToken());
            response.setEstado("ACEPTADA");

            return response;
        }
}

