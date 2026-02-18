package com.example.genesisclub.genesisClub.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.SolicitudDTO;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;
import com.example.genesisclub.genesisClub.Servicio.SolicitudSerSocioService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/solicitud")
public class SolicitudSocioController {

    @Autowired
    private SolicitudSerSocioService solicitudSerSocioService;

    @PostMapping("/nuevo")
    public ResponseEntity<ResponceDTO> crearSolicitud(@RequestBody SolicitudDTO solicitud) {
        
        ResponceDTO response = solicitudSerSocioService.crearSolicitud(solicitud, null);

        // Si el Service detectó que el email ya existe (Usuario o Solicitud)
        if (response.getNumOfErrors() > 0) {
            // Devolvemos 400 Bad Request para que Angular sepa que es un error de validación
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Si se creó correctamente, devolvemos 201 Created (es más preciso que OK para creaciones)
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/pendientes")
@PreAuthorize("hasRole('ADMIN')") // solo admin puede acceder
public ResponseEntity<?> obtenerSolicitudesPendientes() {
    List<SolicitudDTO> pendientes = solicitudSerSocioService.obtenerSolicitudesPendientes();

    if (pendientes.isEmpty()) {
        // Devolver un mensaje si no hay solicitudes pendientes
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "No hay solicitudes pendientes en este momento.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Si hay solicitudes pendientes, devolver la lista
    return new ResponseEntity<>(pendientes, HttpStatus.OK);
}


    @PutMapping("/actualizar/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ResponceDTO> actualizarEstadoSolicitud(
        @PathVariable Long id,
        @RequestParam String nuevoEstado
) {
    EstadoSolicitudEnums estadoEnum;

    try {
        estadoEnum = EstadoSolicitudEnums.valueOf(nuevoEstado.toUpperCase());
    } catch (IllegalArgumentException e) {
        ResponceDTO error = new ResponceDTO();
        error.setNumOfErrors(1);
        error.setMensage("Estado inválido: " + nuevoEstado);
        return ResponseEntity.badRequest().body(error);
    }

    ResponceDTO response = solicitudSerSocioService.actualizarEstadoSolicitud(id, estadoEnum);
    return ResponseEntity.ok(response);
}

}

