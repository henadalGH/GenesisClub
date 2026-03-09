package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;
import java.util.Optional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroAccesoLogEntity;

public interface RubroAccesoLogService {

    List<RubroAccesoLogEntity> findAllRubroAccesoLog();

    Optional<RubroAccesoLogEntity> findById(Long id);

    RubroAccesoLogEntity save(RubroAccesoLogEntity rubroAccesoLog);

    void deleteById(Long id);

}