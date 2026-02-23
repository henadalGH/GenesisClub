package com.example.genesisclub.genesisClub.Modelo.DTO;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private EstadoSocioEnums estado;
}

