package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.InvitacionEntity;

@Repository
public interface InvitacionRepository extends JpaRepository<InvitacionEntity, Long> {

    Optional<InvitacionEntity> findByToken(String token);

}
