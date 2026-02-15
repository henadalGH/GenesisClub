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
import com.example.genesisclub.genesisClub.Servicio.InvitacionService;


@Service
public class InvitacionServiceImpl implements InvitacionService {

    @Autowired
    private InvitacionRepository invitacionRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private EstadoInvitacionRepository estadoRepository;

    @Override
    public InvitacionResponseDTO crearInvitacion(Long socioId, InvitacionRequestDTO dto) {

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

        // 5️⃣ mapear respuesta
        InvitacionResponseDTO response = new InvitacionResponseDTO();
        response.setId(invitacion.getId());
        response.setEmailDestino(invitacion.getEmailDestino());
        response.setToken(invitacion.getToken());
        response.setEstado(estado.getEstado().name());

        return response;
    }

    @Override
    public InvitacionResponseDTO aceptarInvitacion(String token) {

        InvitacionEntity invitacion = invitacionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invitación no válida"));

        // validar expiración
        if (invitacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La invitación expiró");
        }

        // validar estado
        if (!invitacion.getEstado().getEstado().equals(EstadoinvitacionEnums.PENDIENTE)) {
            throw new RuntimeException("La invitación ya fue usada");
        }

        // buscar estado ACEPTADA
        EstadoInvitacionEntity estadoAceptado = estadoRepository
                .findByEstado(EstadoinvitacionEnums.ACEPTADA)
                .orElseThrow();

        invitacion.setEstado(estadoAceptado);

        invitacionRepository.save(invitacion);

        InvitacionResponseDTO response = new InvitacionResponseDTO();
        response.setId(invitacion.getId());
        response.setEmailDestino(invitacion.getEmailDestino());
        response.setToken(invitacion.getToken());
        response.setEstado("ACEPTADA");

        return response;
    }

}
