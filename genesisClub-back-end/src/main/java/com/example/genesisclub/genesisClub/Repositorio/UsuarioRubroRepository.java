package com.example.genesisclub.genesisClub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioRubroEntity;

@Repository
public interface UsuarioRubroRepository extends JpaRepository<UsuarioRubroEntity, Long> {

	List<UsuarioRubroEntity> findByRubroId(Long idRubro);
	
	List<UsuarioRubroEntity> findByUsuarioId(Long idUsuario);
	
	List<UsuarioRubroEntity> findByActivo(boolean activo);
	
	@Query("SELECT ur FROM UsuarioRubroEntity ur WHERE ur.rubro.id = :idRubro AND ur.activo = true")
	List<UsuarioRubroEntity> findUsuariosActivosPorRubro(@Param("idRubro") Long idRubro);
	
	@Query("SELECT ur FROM UsuarioRubroEntity ur WHERE ur.usuario.id = :idUsuario AND ur.activo = true")
	List<UsuarioRubroEntity> findRubrosActivosPorUsuario(@Param("idUsuario") Long idUsuario);
	
	boolean existsByRubroIdAndUsuarioId(Long idRubro, Long idUsuario);

}