package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.VehiculoEntity;

@Repository
public interface VehiculoRepository extends JpaRepository<VehiculoEntity, Long> {
    Optional<VehiculoEntity> findByPatente(String patente);
}