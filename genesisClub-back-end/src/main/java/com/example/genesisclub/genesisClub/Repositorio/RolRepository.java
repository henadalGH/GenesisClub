package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RolEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, Long>{

    boolean existsByNombre(RolesEnums nombre);

    Optional<RolEntity> findByNombre(RolesEnums nombre);

}
