package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UbicacionEntity;

public interface UbicacionService {

    UbicacionEntity asignarUbicacionPorTelefono(String codigoArea, String numero);
    

}