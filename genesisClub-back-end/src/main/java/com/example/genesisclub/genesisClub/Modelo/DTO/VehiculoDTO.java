package com.example.genesisclub.genesisClub.Modelo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoDTO {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private boolean tieneGnc;
}
