package com.example.genesisclub.genesisClub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.AdminEntity;

@Repository 
public interface AdminRepository extends JpaRepository<AdminEntity, Long>{

    Optional<AdminEntity> findByUsuario_Id(Long usuarioId);

}
