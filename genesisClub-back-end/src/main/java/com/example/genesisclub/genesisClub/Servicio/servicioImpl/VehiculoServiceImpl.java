package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.Entidad.VehiculoEntity;
import com.example.genesisclub.genesisClub.Repositorio.VehiculoRepository;
import com.example.genesisclub.genesisClub.Servicio.VehiculoService;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Override
    public List<VehiculoEntity> findAllVehiculo() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Optional<VehiculoEntity> findById(Long id) {
        return vehiculoRepository.findById(id);
    }

    @Override
    public VehiculoEntity save(VehiculoEntity vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public void deleteById(Long id) {
        vehiculoRepository.deleteById(id);
    }

}