package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioRubroEntity;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRubroRepository;
import com.example.genesisclub.genesisClub.Servicio.UsuarioRubroService;

@Service
@Transactional(readOnly = true)
public class UsuarioRubroServiceImpl implements UsuarioRubroService {

    @Autowired
    private UsuarioRubroRepository usuarioRubroRepository;

    @Override
    public List<UsuarioRubroEntity> findAllUsuarioRubro() {
        return usuarioRubroRepository.findAll();
    }

    @Override
    public Optional<UsuarioRubroEntity> findById(Long id) {
        return usuarioRubroRepository.findById(id);
    }

    @Override
    public UsuarioRubroEntity save(UsuarioRubroEntity usuarioRubro) {
        return usuarioRubroRepository.save(usuarioRubro);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRubroRepository.deleteById(id);
    }

    @Override
    public List<UsuarioRubroEntity> findByRubroId(Long idRubro) {
        return usuarioRubroRepository.findByRubroId(idRubro);
    }

    @Override
    public List<UsuarioRubroEntity> findByUsuarioId(Long idUsuario) {
        return usuarioRubroRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public List<UsuarioRubroEntity> findByActivo(boolean activo) {
        return usuarioRubroRepository.findByActivo(activo);
    }

    @Override
    public List<UsuarioRubroEntity> findUsuariosActivosPorRubro(Long idRubro) {
        return usuarioRubroRepository.findUsuariosActivosPorRubro(idRubro);
    }

    @Override
    public List<UsuarioRubroEntity> findRubrosActivosPorUsuario(Long idUsuario) {
        return usuarioRubroRepository.findRubrosActivosPorUsuario(idUsuario);
    }

    @Override
    public boolean existsByRubroAndUsuario(Long idRubro, Long idUsuario) {
        return usuarioRubroRepository.existsByRubroIdAndUsuarioId(idRubro, idUsuario);
    }

}