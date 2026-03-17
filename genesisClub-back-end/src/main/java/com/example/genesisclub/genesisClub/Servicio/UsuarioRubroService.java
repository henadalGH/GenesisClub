package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;
import java.util.Optional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioRubroEntity;

public interface UsuarioRubroService {

    List<UsuarioRubroEntity> findAllUsuarioRubro();

    Optional<UsuarioRubroEntity> findById(Long id);

    UsuarioRubroEntity save(UsuarioRubroEntity usuarioRubro);

    void deleteById(Long id);
    
    List<UsuarioRubroEntity> findByRubroId(Long idRubro);
    
    List<UsuarioRubroEntity> findByUsuarioId(Long idUsuario);
    
    List<UsuarioRubroEntity> findByActivo(boolean activo);
    
    List<UsuarioRubroEntity> findUsuariosActivosPorRubro(Long idRubro);
    
    List<UsuarioRubroEntity> findRubrosActivosPorUsuario(Long idUsuario);
    
    boolean existsByRubroAndUsuario(Long idRubro, Long idUsuario);

}