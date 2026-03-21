package com.example.genesisclub.genesisClub.Servicio;

import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudJugadorDTO;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;

public interface SolicitudJugadorService {

    ResponceDTO solicitarJugador(SolicitudJugadorDTO dto);

    List<SolicitudJugadorDTO> obtenerSolicitudesPendientesJugador();

    ResponceDTO actualizarEstadoSolicitudJugador(Long solicitudId, EstadoSolicitudEnums nuevoEstado);
}
