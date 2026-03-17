package com.example.genesisclub.genesisClub.Servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroAccesoLogEntity;

public interface RubroAccesoLogService {

    List<RubroAccesoLogEntity> findAllRubroAccesoLog();

    Optional<RubroAccesoLogEntity> findById(Long id);

    RubroAccesoLogEntity save(RubroAccesoLogEntity rubroAccesoLog);

    void deleteById(Long id);
    
    List<RubroAccesoLogEntity> findByRubroId(Long idRubro);
    
    List<RubroAccesoLogEntity> findByUsuarioId(Long idUsuario);
    
    List<RubroAccesoLogEntity> findByExitoso(boolean exitoso);
    
    List<RubroAccesoLogEntity> findAccesosPorRubro(Long idRubro);
    
    List<RubroAccesoLogEntity> findAccesosPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<RubroAccesoLogEntity> findAccesosExitosasPorRubro(Long idRubro);

}