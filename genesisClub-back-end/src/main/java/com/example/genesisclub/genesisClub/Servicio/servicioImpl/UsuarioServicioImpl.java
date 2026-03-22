package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.DTO.UsuarioMapaDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.UsuarioServicio;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // =============================
    // LISTAR TODOS
    // =============================
    @Override
    public List<UsuarioEntity> findAllUsuario() {
        return usuarioRepository.findAll();
    }

    // =============================
    // FILTROS
    // =============================
    @Override
    public List<UsuarioEntity> buscarPorProvincia(String provincia) {
        return usuarioRepository.findByUbicacionProvincia(provincia);
    }

    @Override
    public List<UsuarioEntity> buscarPorZona(String zona) {
        return usuarioRepository.findByUbicacionZona(zona);
    }

    // =============================
    // MAPA (CLAVE)
    // =============================
    @Override
    public List<UsuarioMapaDTO> buscarUsuariosParaMapaPorZona(String zona) {

        return usuarioRepository.findByUbicacionZona(zona)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // =============================
    // MAPPER PRIVADO
    // =============================
    private UsuarioMapaDTO mapToDTO(UsuarioEntity u) {

        UsuarioMapaDTO dto = new UsuarioMapaDTO();

        dto.setNombre(u.getNombre());

        if (u.getUbicacion() != null) {
            dto.setCiudad(u.getUbicacion().getCiudad());
            dto.setProvincia(u.getUbicacion().getProvincia());

            if (u.getUbicacion().getLatitud() != null) {
                dto.setLatitud(u.getUbicacion().getLatitud().doubleValue());
            }

            if (u.getUbicacion().getLongitud() != null) {
                dto.setLongitud(u.getUbicacion().getLongitud().doubleValue());
            }
        }

        return dto;
    }
}