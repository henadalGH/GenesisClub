package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSocioEnitity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;

@Repository
public interface EstadoSocioRepository extends JpaRepository<EstadoSocioEnitity, Long>{
    Optional<EstadoSocioEnitity> findByEstado(EstadoSocioEnums estado);
}
