package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroSocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudRubroEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.EstadoSolicitudRubro;
import com.example.genesisclub.genesisClub.Repositorio.RubroRepository;
import com.example.genesisclub.genesisClub.Repositorio.RubroSocioReository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudRubroRepository;
import com.example.genesisclub.genesisClub.Servicio.SolicitudRubroService;

@Service
@Transactional
public class SolicitudRubroServiceImpl implements SolicitudRubroService {

    @Autowired
    private SolicitudRubroRepository solicitudRubroRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private RubroRepository rubroRepository;

    @Autowired
    private RubroSocioReository rubroSocioRepository;

    @Override
    public SolicitudRubroEntity crearSolicitud(Long socioId, Long rubroId, String claveIngresada) {
        // Validar que el socio existe
        SocioEntity socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + socioId));

        // Validar que el rubro existe
        RubroEntity rubro = rubroRepository.findById(rubroId)
                .orElseThrow(() -> new RuntimeException("Rubro no encontrado con id: " + rubroId));

        // Validar clave de acceso
        if (!claveIngresada.equals(rubro.getClaveAcceso())) {
            throw new RuntimeException("Clave de acceso incorrecta");
        }

        // Evitar solicitudes duplicadas (mismo socio y rubro pendientes o aprobadas)
        if (solicitudRubroRepository.existsBySocioIdAndRubroIdAndEstado(socioId, rubroId, EstadoSolicitudRubro.PENDIENTE) ||
            solicitudRubroRepository.existsBySocioIdAndRubroIdAndEstado(socioId, rubroId, EstadoSolicitudRubro.APROBADA)) {
            throw new RuntimeException("Ya existe una solicitud pendiente o aprobada para este socio y rubro");
        }

        // Verificar si ya existe relación RubroSocio
        boolean existeRelacion = rubroSocioRepository.findAll().stream()
                .anyMatch(rs -> rs.getSocio().getId().equals(socioId) && rs.getRubro().getId().equals(rubroId));
        if (existeRelacion) {
            throw new RuntimeException("El socio ya está asociado a este rubro");
        }

        // Crear solicitud
        SolicitudRubroEntity solicitud = new SolicitudRubroEntity();
        solicitud.setSocio(socio);
        solicitud.setRubro(rubro);
        solicitud.setEstado(EstadoSolicitudRubro.PENDIENTE);
        solicitud.setFechaCreacion(LocalDate.now());

        return solicitudRubroRepository.save(solicitud);
    }

    @Override
    public SolicitudRubroEntity aprobarSolicitud(Long solicitudId) {
        SolicitudRubroEntity solicitud = solicitudRubroRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + solicitudId));

        if (solicitud.getEstado() != EstadoSolicitudRubro.PENDIENTE) {
            throw new RuntimeException("La solicitud no está en estado pendiente");
        }

        // Cambiar estado
        solicitud.setEstado(EstadoSolicitudRubro.APROBADA);
        solicitudRubroRepository.save(solicitud);

        // Crear relación RubroSocio
        RubroSocioEntity rubroSocio = new RubroSocioEntity();
        rubroSocio.setSocio(solicitud.getSocio());
        rubroSocio.setRubro(solicitud.getRubro());
        rubroSocio.setFechaAsignacion(LocalDate.now());
        rubroSocioRepository.save(rubroSocio);

        return solicitud;
    }

    @Override
    public SolicitudRubroEntity rechazarSolicitud(Long solicitudId) {
        SolicitudRubroEntity solicitud = solicitudRubroRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + solicitudId));

        if (solicitud.getEstado() != EstadoSolicitudRubro.PENDIENTE) {
            throw new RuntimeException("La solicitud no está en estado pendiente");
        }

        // Cambiar estado
        solicitud.setEstado(EstadoSolicitudRubro.RECHAZADA);
        return solicitudRubroRepository.save(solicitud);
    }

    @Override
    public List<SolicitudRubroEntity> obtenerSolicitudesPendientes() {
        return solicitudRubroRepository.findAll().stream()
                .filter(s -> s.getEstado() == EstadoSolicitudRubro.PENDIENTE)
                .toList();
    }

}