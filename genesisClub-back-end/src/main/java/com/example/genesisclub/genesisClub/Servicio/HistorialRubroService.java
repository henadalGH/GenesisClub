package com.example.genesisclub.genesisClub.Servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.HistorialRubroEntity;

public interface HistorialRubroService {

    List<HistorialRubroEntity> findAll();

    Optional<HistorialRubroEntity> findById(Long id);
    
    List<HistorialRubroEntity> findByRubroId(Long idRubro);
    
    List<HistorialRubroEntity> findByAdminId(Long idAdmin);

    HistorialRubroEntity save(HistorialRubroEntity historialRubro);

    void deleteById(Long id);
    
    List<HistorialRubroEntity> findHistorialPorFechas(LocalDate fechaInicio, LocalDate fechaFin);

}
