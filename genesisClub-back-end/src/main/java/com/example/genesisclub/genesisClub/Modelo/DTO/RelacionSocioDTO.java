package com.example.genesisclub.genesisClub.Modelo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelacionSocioDTO {
    private Long idRelacion;
    private Long idSocioHijo;
    private String nombreCompleto;
    private Integer nivel;
    private LocalDate fechaIngreso;
    // Esta es la clave: cada nodo tiene su propia lista de descendientes
    private List<RelacionSocioDTO> descendientes = new ArrayList<>();

    // Constructor para mapeo rápido sin descendientes inicialmente
    public RelacionSocioDTO(Long idRelacion, Long idSocioHijo, String nombreCompleto, Integer nivel, LocalDate fechaIngreso) {
        this.idRelacion = idRelacion;
        this.idSocioHijo = idSocioHijo;
        this.nombreCompleto = nombreCompleto;
        this.nivel = nivel;
        this.fechaIngreso = fechaIngreso;
    }
}