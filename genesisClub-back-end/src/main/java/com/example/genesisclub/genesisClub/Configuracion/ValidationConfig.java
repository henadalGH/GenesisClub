package com.example.genesisclub.genesisClub.Configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.genesisclub.genesisClub.Modelo.Validacion.UsuarioValidacion;

@Configuration
public class ValidationConfig {

    @Bean
    UsuarioValidacion usuarioValidacion()
    {
        return new UsuarioValidacion();
    }
    
}