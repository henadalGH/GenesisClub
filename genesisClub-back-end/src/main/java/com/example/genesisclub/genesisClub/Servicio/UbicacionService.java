package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;
import java.util.Optional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UbicacionEntity;

public interface UbicacionService {

    List<UbicacionEntity> findAllUbicacion();

    Optional<UbicacionEntity> findById(Long id);

    UbicacionEntity save(UbicacionEntity ubicacion);

    void deleteById(Long id);

}