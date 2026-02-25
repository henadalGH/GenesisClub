package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;

import lombok.Data;


@Data
public class RegistroDTO {
    
    //Usuario comun
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private LocalDate fechaCreacion;
    private RolesEnums rol;

    
    private EstadoSocioEnums estado;
    
    }



