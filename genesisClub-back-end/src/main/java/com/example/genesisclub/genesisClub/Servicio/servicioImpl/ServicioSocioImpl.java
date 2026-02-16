package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.DTO.SocioDTO;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Servicio.SocioServicio;

@Service
public class ServicioSocioImpl implements SocioServicio{

    @Autowired
    private SocioRepository socioRepository;

    @Override
public List<SocioDTO> obtenerSocio() {
    return socioRepository.findAll()
        .stream()
        .map(s -> new SocioDTO(
            s.getId(),
            s.getUsuario().getNombre(), 
            s.getUsuario().getApellido(),
            s.getUsuario().getEmail()
        ))
        .toList();
}


}
