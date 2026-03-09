package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioRubroEntity;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRubroRepository;
import com.example.genesisclub.genesisClub.Servicio.UsuarioRubroService;

@Service
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

}