package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;

public interface RegistroUsuarioServicio {

    public ResponceDTO registrarUsuario(RegistroDTO usuarioDTO, RolesEnums rolEnum, EstadoSocioEnums estadoSocioEnum) throws Exception;

}
