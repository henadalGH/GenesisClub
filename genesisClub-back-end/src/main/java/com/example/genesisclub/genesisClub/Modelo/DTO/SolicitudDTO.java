package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import lombok.Data;

@Data
public class SolicitudDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String contacto;
    private LocalDate fechaSolicitud;
    private EstadoSolicitudEnums estado;
    private String token;

}
