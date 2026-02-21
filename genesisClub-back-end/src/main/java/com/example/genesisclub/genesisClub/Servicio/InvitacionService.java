package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.DTO.Reques.InvitacionRequestDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.Responce.InvitacionResponseDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;

public interface InvitacionService {

    InvitacionResponseDTO crearInvitacion(SocioEntity socioOrigen, InvitacionRequestDTO dto);

    InvitacionResponseDTO aceptarInvitacion(String token);
}