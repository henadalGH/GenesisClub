package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.SocioDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.VehiculoDTO;



public interface SocioServicio {

    List<SocioDTO> obtenerSocio();
    public SocioDTO obtenerPorId(Long id);
    List<VehiculoDTO> obtenerVehiculosPorSocio(Long id);
    void suspenderSocio(Long id);
    void bloquearSocio(Long id);
    void activarSocio(Long id);
}
