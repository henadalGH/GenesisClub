package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import lombok.Data;

@Data
public class SolicitudJugadorDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String contacto;
    private LocalDate fechaSolicitud;
    private EstadoSolicitudEnums estado;
    private String token;

    // campos relacionados al vehículo (idénticos a los que tendría la versión de socio si existieran)
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private Boolean tieneGnc;
}