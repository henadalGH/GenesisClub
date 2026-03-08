package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.DTO.RelacionSocioDTO;
import java.util.List;

public interface RelacionSocioService {
    // Devuelve el árbol completo partiendo de un socio
    RelacionSocioDTO obtenerArbolPorSocio(Long socioId);
    
    // Opcional: Lista plana de todos los descendientes (si no quieres árbol)
    List<RelacionSocioDTO> obtenerListaDescendientes(Long socioId);

    public List<RelacionSocioDTO> obtenerReferidosDeSocio(Long socioId);
}