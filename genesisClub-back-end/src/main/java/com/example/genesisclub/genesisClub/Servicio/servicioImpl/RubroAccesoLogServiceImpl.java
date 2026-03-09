package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroAccesoLogEntity;
import com.example.genesisclub.genesisClub.Repositorio.RubroAccesoLogRepository;
import com.example.genesisclub.genesisClub.Servicio.RubroAccesoLogService;

@Service
public class RubroAccesoLogServiceImpl implements RubroAccesoLogService {

    @Autowired
    private RubroAccesoLogRepository rubroAccesoLogRepository;

    @Override
    public List<RubroAccesoLogEntity> findAllRubroAccesoLog() {
        return rubroAccesoLogRepository.findAll();
    }

    @Override
    public Optional<RubroAccesoLogEntity> findById(Long id) {
        return rubroAccesoLogRepository.findById(id);
    }

    @Override
    public RubroAccesoLogEntity save(RubroAccesoLogEntity rubroAccesoLog) {
        return rubroAccesoLogRepository.save(rubroAccesoLog);
    }

    @Override
    public void deleteById(Long id) {
        rubroAccesoLogRepository.deleteById(id);
    }

}