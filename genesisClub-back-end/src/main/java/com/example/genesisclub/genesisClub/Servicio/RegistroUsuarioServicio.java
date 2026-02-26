package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;

public interface RegistroUsuarioServicio {

    ResponceDTO registrarUsuario(RegistroDTO dto);

    ResponceDTO registrarDesdeSolicitud(RegistroDTO dto); // bypass validación solicitud
}
