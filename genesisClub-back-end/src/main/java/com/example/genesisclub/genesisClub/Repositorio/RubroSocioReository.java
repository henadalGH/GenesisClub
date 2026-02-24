package com.example.genesisclub.genesisClub.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroSocioEntity;

@Repository
public interface RubroSocioReository extends JpaRepository<RubroSocioEntity, Long> {

}
