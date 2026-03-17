package com.example.genesisclub.genesisClub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroSocioEntity;

@Repository
public interface RubroSocioReository extends JpaRepository<RubroSocioEntity, Long> {

	List<RubroSocioEntity> findByRubroId(Long idRubro);
	
	List<RubroSocioEntity> findBySocioId(Long idSocio);
	
	@Query("SELECT rs FROM RubroSocioEntity rs WHERE rs.rubro.id = :idRubro AND rs.socio.id = :idSocio")
	List<RubroSocioEntity> findByRubroIdAndSocioId(@Param("idRubro") Long idRubro, @Param("idSocio") Long idSocio);
	
	boolean existsByRubroIdAndSocioId(Long idRubro, Long idSocio);

}
