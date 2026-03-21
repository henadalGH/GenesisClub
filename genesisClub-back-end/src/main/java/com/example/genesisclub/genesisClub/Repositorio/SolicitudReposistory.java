package com.example.genesisclub.genesisClub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.TipoSolicitud;

@Repository
public interface SolicitudReposistory extends JpaRepository<SolicitudEntity, Long> {
    List<SolicitudEntity> findByEstado(EstadoSolicitudEntity estado);
    List<SolicitudEntity> findByEstadoAndTipoSolicitud(EstadoSolicitudEntity estado, TipoSolicitud tipo);
    boolean existsByEmail(String email);

    // devuelve la última solicitud registrada para un email, útil para obtener la patente
    java.util.Optional<SolicitudEntity> findTopByEmailOrderByFechaSolicitudDesc(String email);

}
