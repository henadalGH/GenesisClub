package com.example.genesisclub.genesisClub.Repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.HistorialRubroEntity;

@Repository
public interface HistorialRubroRepository extends JpaRepository<HistorialRubroEntity, Long>{
	
	List<HistorialRubroEntity> findByRubroId(Long idRubro);
	
	List<HistorialRubroEntity> findByAdminId(Long idAdmin);
	
	@Query("SELECT h FROM HistorialRubroEntity h WHERE h.rubro.id = :idRubro ORDER BY h.fecha DESC")
	List<HistorialRubroEntity> findHistorialPorRubro(@Param("idRubro") Long idRubro);
	
	@Query("SELECT h FROM HistorialRubroEntity h WHERE h.fecha BETWEEN :fechaInicio AND :fechaFin")
	List<HistorialRubroEntity> findHistorialPorFechas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

}
