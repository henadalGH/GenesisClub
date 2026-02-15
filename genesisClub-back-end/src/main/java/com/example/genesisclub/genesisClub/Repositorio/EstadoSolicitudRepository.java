package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;

@Repository
public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitudEntity, Long> {

    Optional<EstadoSolicitudEntity> findByEstado(EstadoSolicitudEnums estado);
}
