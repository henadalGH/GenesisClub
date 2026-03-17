package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.Entidad.HistorialRubroEntity;
import com.example.genesisclub.genesisClub.Repositorio.HistorialRubroRepository;
import com.example.genesisclub.genesisClub.Servicio.HistorialRubroService;

@Service
public class HistorialRubroServiceImpl implements HistorialRubroService {

    @Autowired
    private HistorialRubroRepository historialRubroRepository;

    @Override
    public List<HistorialRubroEntity> findAll() {
        return historialRubroRepository.findAll();
    }

    @Override
    public Optional<HistorialRubroEntity> findById(Long id) {
        return historialRubroRepository.findById(id);
    }

    @Override
    public List<HistorialRubroEntity> findByRubroId(Long idRubro) {
        return historialRubroRepository.findByRubroId(idRubro);
    }

    @Override
    public List<HistorialRubroEntity> findByAdminId(Long idAdmin) {
        return historialRubroRepository.findByAdminId(idAdmin);
    }

    @Override
    public HistorialRubroEntity save(HistorialRubroEntity historialRubro) {
        if (historialRubro.getFecha() == null) {
            historialRubro.setFecha(LocalDate.now());
        }
        return historialRubroRepository.save(historialRubro);
    }

    @Override
    public void deleteById(Long id) {
        historialRubroRepository.deleteById(id);
    }

    @Override
    public List<HistorialRubroEntity> findHistorialPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return historialRubroRepository.findHistorialPorFechas(fechaInicio, fechaFin);
    }

}
