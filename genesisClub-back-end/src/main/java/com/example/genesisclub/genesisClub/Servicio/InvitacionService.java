package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.Responce.InvitacionResponseDTO;

import jakarta.mail.MessagingException;

public interface InvitacionService {

    InvitacionResponseDTO crearInvitacion(Long socioId, InvitacionRequestDTO dto)throws MessagingException;
    InvitacionResponseDTO aceptarInvitacion(String token);
    

}
