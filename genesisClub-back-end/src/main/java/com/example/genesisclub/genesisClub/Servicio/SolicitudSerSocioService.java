package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudDTO;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;

public interface SolicitudSerSocioService {

    ResponceDTO crearSolicitud(SolicitudDTO solicitud, EstadoSolicitudEnums estadoSolicitud);
    public List<SolicitudDTO> obtenerSolicitudesPendientes();
    public ResponceDTO actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitudEnums nuevoEstado);
    

}
