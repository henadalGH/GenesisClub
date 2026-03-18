package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.JugadorEntity;

@Repository
public interface JugadorRepository extends JpaRepository<JugadorEntity, Long>{

    Optional<JugadorEntity> findByUsuario_Id(Long usuarioId);

}
