package com.example.genesisclub.genesisClub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudEntity;

@Repository
public interface SolicitudReposistory extends JpaRepository<SolicitudEntity, Long> {
    List<SolicitudEntity> findByEstado(EstadoSolicitudEntity estado);

}
