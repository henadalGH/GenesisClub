package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UbicacionEntity;
import com.example.genesisclub.genesisClub.Repositorio.UbicacionRepository;
import com.example.genesisclub.genesisClub.Servicio.UbicacionService;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Override
    public List<UbicacionEntity> findAllUbicacion() {
        return ubicacionRepository.findAll();
    }

    @Override
    public Optional<UbicacionEntity> findById(Long id) {
        return ubicacionRepository.findById(id);
    }

    @Override
    public UbicacionEntity save(UbicacionEntity ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @Override
    public void deleteById(Long id) {
        ubicacionRepository.deleteById(id);
    }

}