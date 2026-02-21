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
import com.example.genesisclub.genesisClub.Servicio.EmailService;
import com.example.genesisclub.genesisClub.Servicio.InvitacionService;

@Service
public class InvitacionServiceImpl implements InvitacionService {

    @Autowired
    private InvitacionRepository invitacionRepository;

    @Autowired
    private EstadoInvitacionRepository estadoRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public InvitacionResponseDTO crearInvitacion(
            SocioEntity socioOrigen,
            InvitacionRequestDTO dto) {

        // 1️⃣ estado PENDIENTE
        EstadoInvitacionEntity estado = estadoRepository
                .findByEstado(EstadoinvitacionEnums.PENDIENTE)
                .orElseThrow(() -> new RuntimeException("Estado no configurado"));

        // 2️⃣ token único
        String token = UUID.randomUUID().toString();

        // 3️⃣ crear invitación
        InvitacionEntity invitacion = new InvitacionEntity();
        invitacion.setSocioOrigen(socioOrigen);
        invitacion.setEmailDestino(dto.getEmailDestino());
        invitacion.setToken(token);
        invitacion.setFechaExpiracion(LocalDateTime.now().plusDays(2));
        invitacion.setEstado(estado);

        invitacionRepository.save(invitacion);

        // 4️⃣ enviar mail
        String link = "http://localhost:4200/registro-invitado?token=" + token;
        emailService.enviarInvitacion(dto.getEmailDestino(), link);

        // 5️⃣ response
        InvitacionResponseDTO response = new InvitacionResponseDTO();
        response.setId(invitacion.getId());
        response.setEmailDestino(invitacion.getEmailDestino());
        response.setToken(token);
        response.setEstado(estado.getEstado().name());

        return response;
    }

    @Override
    public InvitacionResponseDTO aceptarInvitacion(String token) {

        InvitacionEntity invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitación no válida"));

        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La invitación expiró");
        }

        if (!invitacion.getEstado().getEstado().equals(EstadoinvitacionEnums.PENDIENTE)) {
            throw new RuntimeException("La invitación ya fue usada");
        }

        EstadoInvitacionEntity estadoAceptado = estadoRepository
                .findByEstado(EstadoinvitacionEnums.ACEPTADA)
                .orElseThrow();

        invitacion.setEstado(estadoAceptado);
        invitacionRepository.save(invitacion);

        InvitacionResponseDTO response = new InvitacionResponseDTO();
        response.setId(invitacion.getId());
        response.setEmailDestino(invitacion.getEmailDestino());
        response.setToken(token);
        response.setEstado("ACEPTADA");

        return response;
    }
}