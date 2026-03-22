package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.RubroDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroEntity;
import com.example.genesisclub.genesisClub.Repositorio.RubroRepository;
import com.example.genesisclub.genesisClub.Servicio.RubroService;

@Service
@Transactional(readOnly = true)
public class RubroServiceImpl implements RubroService {

    @Autowired
    private RubroRepository rubroRepository;

    // ===============================
    // OBTENER TODOS LOS RUBROS
    // ===============================
    @Override
    public List<RubroDTO> obtenerTodos() {
        return rubroRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ===============================
    // OBTENER RUBROS ACTIVOS
    // ===============================
    @Override
    public List<RubroDTO> obtenerActivos() {
        return rubroRepository.findByActivo(true)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ===============================
    // OBTENER RUBRO POR ID
    // ===============================
    @Override
    public RubroDTO obtenerPorId(Long id) {
        RubroEntity rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con id: " + id));
        return toDTO(rubro);
    }

    // ===============================
    // OBTENER RUBRO POR NOMBRE
    // ===============================
    @Override
    public RubroDTO obtenerPorNombre(String nombre) {
        RubroEntity rubro = rubroRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con nombre: " + nombre));
        return toDTO(rubro);
    }

    // ===============================
    // OBTENER RUBRO POR CLAVE ACCESO
    // ===============================
    @Override
    public RubroDTO obtenerPorClaveAcceso(String claveAcceso) {
        RubroEntity rubro = rubroRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con clave: " + claveAcceso));
        return toDTO(rubro);
    }

    // ===============================
    // CREAR RUBRO
    // ===============================
    @Override
    @Transactional
    public RubroDTO crear(RubroDTO rubroDTO) {
        RubroEntity rubro = new RubroEntity();
        rubro.setNombre(rubroDTO.getNombre());
        rubro.setDescripcion(rubroDTO.getDescripcion());
        rubro.setFechaCreacion(LocalDate.now());
        rubro.setFechaModificacion(LocalDate.now());
        rubro.setActivo(true);
        rubro.setClaveAcceso(rubroDTO.getClaveAcceso());
        
        RubroEntity rubroCreado = rubroRepository.save(rubro);
        return toDTO(rubroCreado);
    }

    // ===============================
    // ACTUALIZAR RUBRO
    // ===============================
    @Override
    @Transactional
    public RubroDTO actualizar(Long id, RubroDTO rubroDTO) {
        RubroEntity rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con id: " + id));

        if (rubroDTO.getNombre() != null) {
            rubro.setNombre(rubroDTO.getNombre());
        }
        if (rubroDTO.getDescripcion() != null) {
            rubro.setDescripcion(rubroDTO.getDescripcion());
        }
        rubro.setFechaModificacion(LocalDate.now());

        RubroEntity rubroActualizado = rubroRepository.save(rubro);
        return toDTO(rubroActualizado);
    }

    // ===============================
    // ELIMINAR RUBRO
    // ===============================
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!rubroRepository.existsById(id)) {
            throw new RuntimeException("Rubro no encontrado con id: " + id);
        }
        rubroRepository.deleteById(id);
    }

    // ===============================
    // BUSCAR RUBROS POR NOMBRE
    // ===============================
    @Override
    public List<RubroDTO> buscarPorNombre(String nombre) {
        // Escapar caracteres especiales de LIKE
        String escapedNombre = nombre
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        
        return rubroRepository.buscarActivosPorNombre(escapedNombre)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ===============================
    // ACTIVAR RUBRO
    // ===============================
    @Override
    @Transactional
    public RubroDTO activar(Long id) {
        RubroEntity rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con id: " + id));

        rubro.setActivo(true);
        rubro.setFechaModificacion(LocalDate.now());

        RubroEntity rubroActivado = rubroRepository.save(rubro);
        return toDTO(rubroActivado);
    }

    // ===============================
    // DESACTIVAR RUBRO
    // ===============================
    @Override
    @Transactional
    public RubroDTO desactivar(Long id) {
        RubroEntity rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con id: " + id));

        rubro.setActivo(false);
        rubro.setFechaModificacion(LocalDate.now());

        RubroEntity rubroDesactivado = rubroRepository.save(rubro);
        return toDTO(rubroDesactivado);
    }

    // ===============================
    // MAPPER RUBRO -> DTO
    // ===============================
    private RubroDTO toDTO(RubroEntity rubro) {
        return new RubroDTO(
                rubro.getId(),
                rubro.getNombre(),
                rubro.getDescripcion(),
                rubro.isActivo(),
                rubro.getFechaCreacion(),
                rubro.getFechaModificacion(),
                rubro.getAdmin() != null ? rubro.getAdmin().getId() : null,
                rubro.getClaveAcceso(),
                rubro.getFechaClaveGeneracion(),
                rubro.isClaveActiva()
        );
    }

}
