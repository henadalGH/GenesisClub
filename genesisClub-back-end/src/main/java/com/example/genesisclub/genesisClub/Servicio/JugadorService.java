package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.JugadorDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.VehiculoDTO;

public interface JugadorService {
    
    List<JugadorDTO> obtenerTodosLosJugadores();
    
    JugadorDTO obtenerJugadorPorId(Long id);
    
    List<VehiculoDTO> obtenerVehiculosPorJugador(Long id);
    
    void suspenderJugador(Long id);
    
    void bloquearJugador(Long id);
    
    void activarJugador(Long id);
}
