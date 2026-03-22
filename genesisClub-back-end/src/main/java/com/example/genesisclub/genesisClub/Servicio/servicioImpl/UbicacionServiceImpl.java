package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.Entidad.UbicacionEntity;
import com.example.genesisclub.genesisClub.Repositorio.UbicacionRepository;
import com.example.genesisclub.genesisClub.Servicio.UbicacionService;

@Service
public class UbicacionServiceImpl implements UbicacionService {

     @Autowired
    private UbicacionRepository ubicacionRepository;

    @Override
    public UbicacionEntity asignarUbicacionPorTelefono(String codigoArea, String numero) {

        if (codigoArea == null || codigoArea.isEmpty()) {
            throw new RuntimeException("Código de área inválido");
        }

        // 🔥 Limpiar código (0342 → 342)
        String codigoLimpio = codigoArea.replaceFirst("^0+", "");

        List<UbicacionEntity> ubicaciones = ubicacionRepository.findByCodigoArea(codigoLimpio);

        if (ubicaciones.isEmpty()) {
            throw new RuntimeException("No se encontró ubicación para el código de área: " + codigoLimpio);
        }

        // 🧠 Si hay varias ciudades con mismo código
        if (ubicaciones.size() == 1) {
            return ubicaciones.get(0);
        }

        // 👉 lógica simple (por ahora)
        // elegimos la primera (después se puede mejorar)
        return ubicaciones.get(0);
    }
}
