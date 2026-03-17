package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.TipoUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRubroDTO {

    private Long id;

    private boolean activo;

    private LocalDate fechaIngreso;

    private TipoUsuario tipoUsuario;

    private Long idRubro;

    private Long idUsuario;

}