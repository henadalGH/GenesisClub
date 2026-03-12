package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.JugadorDTO;

public interface JugadorService {
    
    List<JugadorDTO> obtenerTodosLosJugadores();
    
    JugadorDTO obtenerJugadorPorId(Long id);
}
