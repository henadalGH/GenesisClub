package com.example.genesisclub.genesisClub.Repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroAccesoLogEntity;

@Repository
public interface RubroAccesoLogRepository extends JpaRepository<RubroAccesoLogEntity, Long> {

	List<RubroAccesoLogEntity> findByRubroId(Long idRubro);
	
	List<RubroAccesoLogEntity> findByUsuarioId(Long idUsuario);
	
	List<RubroAccesoLogEntity> findByExitoso(boolean exitoso);
	
	@Query("SELECT log FROM RubroAccesoLogEntity log WHERE log.rubro.id = :idRubro ORDER BY log.fechaAcceso DESC")
	List<RubroAccesoLogEntity> findAccesosPorRubro(@Param("idRubro") Long idRubro);
	
	@Query("SELECT log FROM RubroAccesoLogEntity log WHERE log.fechaAcceso BETWEEN :fechaInicio AND :fechaFin")
	List<RubroAccesoLogEntity> findAccesosPorFecha(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
	
	@Query("SELECT log FROM RubroAccesoLogEntity log WHERE log.rubro.id = :idRubro AND log.exitoso = false")
	List<RubroAccesoLogEntity> findAccesosExitosasPorRubro(@Param("idRubro") Long idRubro);

}