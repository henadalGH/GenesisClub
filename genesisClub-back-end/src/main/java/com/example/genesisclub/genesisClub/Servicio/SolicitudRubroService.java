package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudRubroEntity;

import java.util.List;

public interface SolicitudRubroService {

    SolicitudRubroEntity crearSolicitud(Long socioId, Long rubroId, String claveIngresada);

    SolicitudRubroEntity aprobarSolicitud(Long solicitudId);

    SolicitudRubroEntity rechazarSolicitud(Long solicitudId);

    List<SolicitudRubroEntity> obtenerSolicitudesPendientes();

}