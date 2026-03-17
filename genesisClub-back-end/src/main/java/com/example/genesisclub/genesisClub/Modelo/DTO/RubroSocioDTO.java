package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RubroSocioDTO {

    private Long id;
    
    private LocalDate fechaAsignacion;
    
    private Long idSocio;
    private String nombreSocio;
    
    private Long idRubro;
    private String nombreRubro;

}
