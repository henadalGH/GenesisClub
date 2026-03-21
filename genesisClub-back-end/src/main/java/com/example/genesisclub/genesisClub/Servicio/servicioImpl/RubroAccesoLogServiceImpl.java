package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDateTime;
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
        if (rubroAccesoLog.getFechaAcceso() == null) {
            rubroAccesoLog.setFechaAcceso(LocalDateTime.now());
        }
        return rubroAccesoLogRepository.save(rubroAccesoLog);
    }

    @Override
    public void deleteById(Long id) {
        rubroAccesoLogRepository.deleteById(id);
    }

    @Override
    public List<RubroAccesoLogEntity> findByRubroId(Long idRubro) {
        return rubroAccesoLogRepository.findByRubroId(idRubro);
    }

    @Override
    public List<RubroAccesoLogEntity> findByUsuarioId(Long idUsuario) {
        return rubroAccesoLogRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public List<RubroAccesoLogEntity> findByExitoso(boolean exitoso) {
        return rubroAccesoLogRepository.findByExitoso(exitoso);
    }

    @Override
    public List<RubroAccesoLogEntity> findAccesosPorRubro(Long idRubro) {
        return rubroAccesoLogRepository.findAccesosPorRubro(idRubro);
    }

    @Override
    public List<RubroAccesoLogEntity> findAccesosPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return rubroAccesoLogRepository.findAccesosPorFecha(fechaInicio, fechaFin);
    }

    @Override
    public List<RubroAccesoLogEntity> findAccesosExitosasPorRubro(Long idRubro) {
        return rubroAccesoLogRepository.findAccesosExitosasPorRubro(idRubro);
    }

}