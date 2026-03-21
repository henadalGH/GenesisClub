package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.RubroSocioDTO;

public interface RubroSocioService {

    List<RubroSocioDTO> obtenerTodos();

    RubroSocioDTO obtenerPorId(Long id);
    
    List<RubroSocioDTO> obtenerPorRubro(Long idRubro);
    
    List<RubroSocioDTO> obtenerPorSocio(Long idSocio);

    RubroSocioDTO crear(RubroSocioDTO rubroSocio);
    
    RubroSocioDTO actualizar(Long id, RubroSocioDTO rubroSocio);

    void eliminar(Long id);
    
    boolean existeRelacion(Long idRubro, Long idSocio);
    
    void eliminarRelacion(Long idRubro, Long idSocio);

    // Asociar socio a rubro usando clave de acceso
    RubroSocioDTO asociarPorClaveAcceso(Long idSocio, String claveAcceso);

}
