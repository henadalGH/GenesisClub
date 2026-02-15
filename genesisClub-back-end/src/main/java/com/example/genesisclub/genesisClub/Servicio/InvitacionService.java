package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.Responce.InvitacionResponseDTO;

public interface InvitacionService {

    InvitacionResponseDTO crearInvitacion(Long socioId, InvitacionRequestDTO dto);
    InvitacionResponseDTO aceptarInvitacion(String token);

}
