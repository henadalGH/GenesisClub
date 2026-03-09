package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;
import java.util.Optional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.VehiculoEntity;

public interface VehiculoService {

    List<VehiculoEntity> findAllVehiculo();

    Optional<VehiculoEntity> findById(Long id);

    VehiculoEntity save(VehiculoEntity vehiculo);

    void deleteById(Long id);

}