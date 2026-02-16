package com.example.genesisclub.genesisClub.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.genesisclub.genesisClub.Modelo.DTO.SocioDTO;
import com.example.genesisclub.genesisClub.Servicio.SocioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/socio")
public class SocioController {

    @Autowired
    private SocioServicio socioServicio;

    @GetMapping("/todos")
public List<SocioDTO> obtenerSocios() {
    return socioServicio.obtenerSocio();
}

    
}
