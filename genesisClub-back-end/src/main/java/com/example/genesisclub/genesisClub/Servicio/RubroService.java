package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.RubroDTO;

public interface RubroService {

    List<RubroDTO> obtenerTodos();
    
    List<RubroDTO> obtenerActivos();

    RubroDTO obtenerPorId(Long id);
    
    RubroDTO obtenerPorNombre(String nombre);
    
    RubroDTO obtenerPorClaveAcceso(String claveAcceso);

    RubroDTO crear(RubroDTO rubro);
    
    RubroDTO actualizar(Long id, RubroDTO rubro);

    void eliminar(Long id);
    
    List<RubroDTO> buscarPorNombre(String nombre);
    
    RubroDTO activar(Long id);
    
    RubroDTO desactivar(Long id);

}
