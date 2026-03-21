package com.example.genesisclub.genesisClub.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.Entidad.RubroAccesoLogEntity;
import com.example.genesisclub.genesisClub.Servicio.RubroAccesoLogService;

@RestController
@RequestMapping("/api/rubro-acceso-log")
public class RubroAccesoLogController {

    @Autowired
    private RubroAccesoLogService rubroAccesoLogService;

    @GetMapping
    public ResponseEntity<List<RubroAccesoLogEntity>> obtenerTodos() {
        List<RubroAccesoLogEntity> logs = rubroAccesoLogService.findAllRubroAccesoLog();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RubroAccesoLogEntity> obtenerPorId(@PathVariable Long id) {
        Optional<RubroAccesoLogEntity> log = rubroAccesoLogService.findById(id);
        return log.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rubro/{idRubro}")
    public ResponseEntity<List<RubroAccesoLogEntity>> obtenerPorRubro(@PathVariable Long idRubro) {
        List<RubroAccesoLogEntity> logs = rubroAccesoLogService.findAccesosPorRubro(idRubro);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<RubroAccesoLogEntity>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        List<RubroAccesoLogEntity> logs = rubroAccesoLogService.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/exitoso/{exitoso}")
    public ResponseEntity<List<RubroAccesoLogEntity>> obtenerPorExito(@PathVariable boolean exitoso) {
        List<RubroAccesoLogEntity> logs = rubroAccesoLogService.findByExitoso(exitoso);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/rubro/{idRubro}/exitosos")
    public ResponseEntity<List<RubroAccesoLogEntity>> obtenerExitososPorRubro(@PathVariable Long idRubro) {
        List<RubroAccesoLogEntity> logs = rubroAccesoLogService.findAccesosExitosasPorRubro(idRubro);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<List<RubroAccesoLogEntity>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<RubroAccesoLogEntity> logs = rubroAccesoLogService.findAccesosPorFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<RubroAccesoLogEntity> crear(@RequestBody RubroAccesoLogEntity log) {
        RubroAccesoLogEntity logCreado = rubroAccesoLogService.save(log);
        return ResponseEntity.status(HttpStatus.CREATED).body(logCreado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rubroAccesoLogService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}