package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoInvitacionEntity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoinvitacionEnums;

@Repository
public interface EstadoInvitacionRepository extends JpaRepository<EstadoInvitacionEntity, Long>{

    Optional<EstadoInvitacionEntity> findByEstado(EstadoinvitacionEnums pendiente);
}
