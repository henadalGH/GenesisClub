package com.example.genesisclub.genesisClub.Modelo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminResumenDTO {
    private long sociosActivos;
    private long jugadoresActivos;
    private long solicitudesPendientes;
    private long solicitudesJugadoresPendientes;
    private long solicitudesRubrosPendientes;
    private long rubrosActivos;
}
