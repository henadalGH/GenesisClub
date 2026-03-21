package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.AccionRubroEnums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialRubroDTO {

    private Long id;

    private AccionRubroEnums accion;

    private String detalle;

    private LocalDate fecha;

    private Long idAdmin;

    private Long idRubro;

}