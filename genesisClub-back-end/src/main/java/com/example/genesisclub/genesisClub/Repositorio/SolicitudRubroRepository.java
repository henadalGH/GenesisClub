package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudRubroEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.EstadoSolicitudRubro;

@Repository
public interface SolicitudRubroRepository extends JpaRepository<SolicitudRubroEntity, Long> {

    Optional<SolicitudRubroEntity> findBySocioIdAndRubroIdAndEstado(Long socioId, Long rubroId, EstadoSolicitudRubro estado);

    boolean existsBySocioIdAndRubroIdAndEstado(Long socioId, Long rubroId, EstadoSolicitudRubro estado);

}