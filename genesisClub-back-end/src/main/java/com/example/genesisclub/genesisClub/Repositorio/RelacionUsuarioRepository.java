package com.example.genesisclub.genesisClub.Repositorio;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RelacionUsuarioEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RelacionUsuarioRepository extends JpaRepository<RelacionUsuarioEntity, Long> {
    
    // El EntityGraph asegura que traiga al Socio y al Usuario vinculado 
    // para evitar que los nombres lleguen en null por el Lazy Loading
    @EntityGraph(attributePaths = {"socioHijo.usuario", "socioPadre.usuario"})
    List<RelacionUsuarioEntity> findBySocioPadreId(Long socioPadreId);
}