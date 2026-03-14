package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.TipoSolicitud;
import lombok.Data;

@Data
public class SolicitudDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String codigoArea;
    private String numeroCelular;
    private LocalDate fechaSolicitud;
    private EstadoSolicitudEnums estado;
    private String token;
    private TipoSolicitud tipoSolicitud;

    // datos de vehículo opcionales (solicitud de socio ahora pide patente igual que jugador)
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    private Boolean tieneGnc;

}
