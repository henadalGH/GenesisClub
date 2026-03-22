package com.example.genesisclub.genesisClub.Controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.genesisclub.genesisClub.Modelo.DTO.CrearSolicitudRubroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudRubroResponseDTO;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.Entidad.SolicitudRubroEntity;
import com.example.genesisclub.genesisClub.Servicio.SolicitudRubroService;

@RestController
@RequestMapping("/api/solicitudes-rubro")
public class SolicitudRubroController {

    private static final Logger log = LoggerFactory.getLogger(SolicitudRubroController.class);

    @Autowired
    private SolicitudRubroService solicitudRubroService;

    @PostMapping
    public ResponseEntity<Object> crearSolicitud(@Valid @RequestBody CrearSolicitudRubroDTO dto) {
        log.info("Creando solicitud rubro: socioId={}, rubroId={}", dto.getSocioId(), dto.getRubroId());
        try {
            SolicitudRubroEntity solicitud = solicitudRubroService.crearSolicitud(dto.getSocioId(), dto.getRubroId(), dto.getClaveAcceso());
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitud);
        } catch (RuntimeException e) {
            log.warn("Error creando solicitud rubro: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("Validacion fallida: {}", errors);
        return ResponseEntity.badRequest().body(java.util.Map.of("error", errors));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Object> aprobarSolicitud(@PathVariable Long id) {
        try {
            SolicitudRubroEntity solicitud = solicitudRubroService.aprobarSolicitud(id);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<Object> rechazarSolicitud(@PathVariable Long id) {
        try {
            SolicitudRubroEntity solicitud = solicitudRubroService.rechazarSolicitud(id);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudRubroResponseDTO>> obtenerSolicitudesPendientes() {
        List<SolicitudRubroResponseDTO> solicitudes = solicitudRubroService.obtenerSolicitudesPendientes();
        return ResponseEntity.ok(solicitudes);
    }

}