package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.UsuarioMapaDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;

public interface UsuarioServicio {

    List<UsuarioEntity> findAllUsuario();
    List<UsuarioEntity> buscarPorProvincia(String provincia);
    List<UsuarioEntity> buscarPorZona(String zona);
    List<UsuarioMapaDTO> buscarUsuariosParaMapaPorZona(String zona);
}
