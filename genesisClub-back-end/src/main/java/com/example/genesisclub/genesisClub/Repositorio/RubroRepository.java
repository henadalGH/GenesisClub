package com.example.genesisclub.genesisClub.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroEntity;

@Repository
public interface RubroRepository extends JpaRepository<RubroEntity, Long> {

	List<RubroEntity> findByActivo(boolean activo);
	
	Optional<RubroEntity> findByNombre(String nombre);
	
	Optional<RubroEntity> findByClaveAcceso(String claveAcceso);
	
	@Query("SELECT r FROM RubroEntity r WHERE r.activo = true ORDER BY r.fechaCreacion DESC")
	List<RubroEntity> findAllActivosOrdenados();
	
	@Query("SELECT r FROM RubroEntity r WHERE r.nombre LIKE %:nombre% AND r.activo = true")
	List<RubroEntity> buscarActivosPorNombre(@Param("nombre") String nombre);

}
