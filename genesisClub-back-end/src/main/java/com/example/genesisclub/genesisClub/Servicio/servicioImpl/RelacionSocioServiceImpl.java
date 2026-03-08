package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import com.example.genesisclub.genesisClub.Modelo.DTO.RelacionSocioDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RelacionUsuarioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Repositorio.RelacionUsuarioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository; // Asegúrate de tener este import
import com.example.genesisclub.genesisClub.Servicio.RelacionSocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelacionSocioServiceImpl implements RelacionSocioService {

    @Autowired
    private RelacionUsuarioRepository relacionRepository;

    @Autowired
    private SocioRepository socioRepository; // Inyectado para buscar al socio raíz

    @Override
    @Transactional(readOnly = true)
    public List<RelacionSocioDTO> obtenerReferidosDeSocio(Long socioId) {
        return relacionRepository.findBySocioPadreId(socioId).stream()
                .map(entidad -> mapToDTO(entidad, 1))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RelacionSocioDTO obtenerArbolPorSocio(Long socioId) {
        // 1. Buscamos los datos del socio raíz en la DB
        SocioEntity socioRaiz = socioRepository.findById(socioId)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado"));

        // 2. Cargamos el DTO raíz con sus datos reales (evita los nulls iniciales)
        RelacionSocioDTO raiz = new RelacionSocioDTO();
        raiz.setIdSocioHijo(socioRaiz.getId());
        
        if (socioRaiz.getUsuario() != null) {
            raiz.setNombreCompleto(socioRaiz.getUsuario().getNombre() + " " + 
                                  socioRaiz.getUsuario().getApellido());
        }
        
        raiz.setNivel(0); // El socio raíz es el punto de partida (Nivel 0)
        raiz.setDescendientes(new ArrayList<>());

        // 3. Iniciamos la carga recursiva de sus invitados
        cargarHijosRecursivo(raiz, 1);
        return raiz;
    }

    private void cargarHijosRecursivo(RelacionSocioDTO padreDTO, int nivelActual) {
        List<RelacionUsuarioEntity> entidadesHijas = relacionRepository.findBySocioPadreId(padreDTO.getIdSocioHijo());

        for (RelacionUsuarioEntity entidad : entidadesHijas) {
            RelacionSocioDTO hijoDTO = mapToDTO(entidad, nivelActual);
            padreDTO.getDescendientes().add(hijoDTO);
            
            cargarHijosRecursivo(hijoDTO, nivelActual + 1);
        }
    }

    private RelacionSocioDTO mapToDTO(RelacionUsuarioEntity r, int nivelCalculado) {
        SocioEntity socioHijo = r.getSocioHijo(); 
        String nombreCompleto = "Nombre no disponible";

        if (socioHijo != null && socioHijo.getUsuario() != null) {
            nombreCompleto = socioHijo.getUsuario().getNombre() + " " + 
                             socioHijo.getUsuario().getApellido();
        }

        return new RelacionSocioDTO(
            r.getId(),
            socioHijo != null ? socioHijo.getId() : null,
            nombreCompleto,
            nivelCalculado,
            r.getFecha()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelacionSocioDTO> obtenerListaDescendientes(Long socioId) {
        RelacionSocioDTO arbol = obtenerArbolPorSocio(socioId);
        List<RelacionSocioDTO> listaPlana = new ArrayList<>();
        aplanarArbol(arbol, listaPlana);
        return listaPlana;
    }

    private void aplanarArbol(RelacionSocioDTO nodo, List<RelacionSocioDTO> lista) {
        for (RelacionSocioDTO hijo : nodo.getDescendientes()) {
            lista.add(hijo);
            aplanarArbol(hijo, lista);
        }
    }
}