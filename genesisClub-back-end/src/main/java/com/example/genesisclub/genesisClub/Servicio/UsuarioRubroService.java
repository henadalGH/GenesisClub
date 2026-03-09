package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;
import java.util.Optional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioRubroEntity;

public interface UsuarioRubroService {

    List<UsuarioRubroEntity> findAllUsuarioRubro();

    Optional<UsuarioRubroEntity> findById(Long id);

    UsuarioRubroEntity save(UsuarioRubroEntity usuarioRubro);

    void deleteById(Long id);

}