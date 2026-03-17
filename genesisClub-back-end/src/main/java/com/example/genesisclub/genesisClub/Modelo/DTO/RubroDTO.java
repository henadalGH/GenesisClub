package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RubroDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;

    private LocalDate fechaCreacion;
    private LocalDate fechaModificacion;

    private Long idCreador;

    private String claveAcceso;
    private LocalDate fechaClaveGeneracion;
    private boolean claveActiva;

}