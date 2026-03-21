package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.RubroSocioDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroSocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Repositorio.RubroRepository;
import com.example.genesisclub.genesisClub.Repositorio.RubroSocioReository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Servicio.RubroSocioService;

@Service
@Transactional(readOnly = true)
public class RubroSocioServiceImpl implements RubroSocioService {

    @Autowired
    private RubroSocioReository rubroSocioRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private RubroRepository rubroRepository;

    // ===============================
    // OBTENER TODOS LOS RUBRO-SOCIO
    // ===============================
    @Override
    public List<RubroSocioDTO> obtenerTodos() {
        return rubroSocioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ===============================
    // OBTENER RUBRO-SOCIO POR ID
    // ===============================
    @Override
    public RubroSocioDTO obtenerPorId(Long id) {
        RubroSocioEntity rubroSocio = rubroSocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubro-Socio no encontrado con id: " + id));
        return toDTO(rubroSocio);
    }

    // ===============================
    // OBTENER RUBRO-SOCIO POR RUBRO
    // ===============================
    @Override
    public List<RubroSocioDTO> obtenerPorRubro(Long idRubro) {
        return rubroSocioRepository.findByRubroId(idRubro)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ===============================
    // OBTENER RUBRO-SOCIO POR SOCIO
    // ===============================
    @Override
    public List<RubroSocioDTO> obtenerPorSocio(Long idSocio) {
        return rubroSocioRepository.findBySocioId(idSocio)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ===============================
    // CREAR RUBRO-SOCIO
    // ===============================
    @Override
    @Transactional
    public RubroSocioDTO crear(RubroSocioDTO rubroSocioDTO) {
        if (rubroSocioDTO.getIdSocio() == null || rubroSocioDTO.getIdRubro() == null) {
            throw new RuntimeException("ID de Socio e ID de Rubro son requeridos");
        }

        // Verificar si la relación ya existe
        if (existeRelacion(rubroSocioDTO.getIdRubro(), rubroSocioDTO.getIdSocio())) {
            throw new RuntimeException("El socio ya está asociado a este rubro");
        }

        RubroSocioEntity rubroSocio = new RubroSocioEntity();
        rubroSocio.setFechaAsignacion(LocalDate.now());

        SocioEntity socio = socioRepository.findById(rubroSocioDTO.getIdSocio())
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + rubroSocioDTO.getIdSocio()));
        rubroSocio.setSocio(socio);

        RubroEntity rubro = rubroRepository.findById(rubroSocioDTO.getIdRubro())
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con id: " + rubroSocioDTO.getIdRubro()));
        rubroSocio.setRubro(rubro);

        RubroSocioEntity rubroSocioCreado = rubroSocioRepository.save(rubroSocio);
        return toDTO(rubroSocioCreado);
    }

    // ===============================
    // ACTUALIZAR RUBRO-SOCIO
    // ===============================
    @Override
    @Transactional
    public RubroSocioDTO actualizar(Long id, RubroSocioDTO rubroSocioDTO) {
        RubroSocioEntity rubroSocio = rubroSocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubro-Socio no encontrado con id: " + id));

        if (rubroSocioDTO.getFechaAsignacion() != null) {
            rubroSocio.setFechaAsignacion(rubroSocioDTO.getFechaAsignacion());
        }

        RubroSocioEntity rubroSocioActualizado = rubroSocioRepository.save(rubroSocio);
        return toDTO(rubroSocioActualizado);
    }

    // ===============================
    // ELIMINAR RUBRO-SOCIO
    // ===============================
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!rubroSocioRepository.existsById(id)) {
            throw new RuntimeException("Rubro-Socio no encontrado con id: " + id);
        }
        rubroSocioRepository.deleteById(id);
    }

    // ===============================
    // VERIFICAR RELACIÓN
    // ===============================
    @Override
    public boolean existeRelacion(Long idRubro, Long idSocio) {
        return rubroSocioRepository.existsByRubroIdAndSocioId(idRubro, idSocio);
    }

    // ===============================
    // ELIMINAR RELACIÓN
    // ===============================
    @Override
    @Transactional
    public void eliminarRelacion(Long idRubro, Long idSocio) {
        List<RubroSocioEntity> rubroSocios = rubroSocioRepository.findByRubroIdAndSocioId(idRubro, idSocio);
        rubroSocioRepository.deleteAll(rubroSocios);
    }

    // ===============================
    // ASOCIAR POR CLAVE DE ACCESO
    // ===============================
    @Override
    @Transactional
    public RubroSocioDTO asociarPorClaveAcceso(Long idSocio, String claveAcceso) {
        // Validar que el socio existe
        SocioEntity socio = socioRepository.findById(idSocio)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + idSocio));

        // Buscar el rubro por clave de acceso
        RubroEntity rubro = rubroRepository.findByClaveAcceso(claveAcceso)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con la clave de acceso proporcionada"));

        // Validar que la clave esté activa
        if (!rubro.isClaveActiva()) {
            throw new RuntimeException("La clave de acceso para este rubro no está activa");
        }

        // Validar que el rubro esté activo
        if (!rubro.isActivo()) {
            throw new RuntimeException("El rubro no está activo");
        }

        // Verificar si la relación ya existe
        if (existeRelacion(rubro.getId(), idSocio)) {
            throw new RuntimeException("El socio ya está asociado a este rubro");
        }

        // Crear la asociación
        RubroSocioEntity rubroSocio = new RubroSocioEntity();
        rubroSocio.setFechaAsignacion(LocalDate.now());
        rubroSocio.setSocio(socio);
        rubroSocio.setRubro(rubro);

        RubroSocioEntity rubroSocioCreado = rubroSocioRepository.save(rubroSocio);
        return toDTO(rubroSocioCreado);
    }

    // ===============================
    // MAPPER RUBRO-SOCIO -> DTO
    // ===============================
    private RubroSocioDTO toDTO(RubroSocioEntity rubroSocio) {
        return new RubroSocioDTO(
                rubroSocio.getId(),
                rubroSocio.getFechaAsignacion(),
                rubroSocio.getSocio() != null ? rubroSocio.getSocio().getId() : null,
                rubroSocio.getSocio() != null ? rubroSocio.getSocio().getUsuario().getNombre() : null,
                rubroSocio.getRubro() != null ? rubroSocio.getRubro().getId() : null,
                rubroSocio.getRubro() != null ? rubroSocio.getRubro().getNombre() : null
        );
    }

}
