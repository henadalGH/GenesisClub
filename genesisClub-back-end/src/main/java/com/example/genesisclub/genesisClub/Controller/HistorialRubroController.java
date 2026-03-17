package com.example.genesisclub.genesisClub.Controller;

import java.time.LocalDate;
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

import com.example.genesisclub.genesisClub.Modelo.Entidad.HistorialRubroEntity;
import com.example.genesisclub.genesisClub.Servicio.HistorialRubroService;

@RestController
@RequestMapping("/api/historial-rubro")
public class HistorialRubroController {

    @Autowired
    private HistorialRubroService historialRubroService;

    @GetMapping
    public ResponseEntity<List<HistorialRubroEntity>> obtenerTodos() {
        List<HistorialRubroEntity> historial = historialRubroService.findAll();
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialRubroEntity> obtenerPorId(@PathVariable Long id) {
        Optional<HistorialRubroEntity> historial = historialRubroService.findById(id);
        return historial.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rubro/{idRubro}")
    public ResponseEntity<List<HistorialRubroEntity>> obtenerPorRubro(@PathVariable Long idRubro) {
        List<HistorialRubroEntity> historial = historialRubroService.findByRubroId(idRubro);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/admin/{idAdmin}")
    public ResponseEntity<List<HistorialRubroEntity>> obtenerPorAdmin(@PathVariable Long idAdmin) {
        List<HistorialRubroEntity> historial = historialRubroService.findByAdminId(idAdmin);
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<List<HistorialRubroEntity>> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<HistorialRubroEntity> historial = historialRubroService.findHistorialPorFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(historial);
    }

    @PostMapping
    public ResponseEntity<HistorialRubroEntity> crear(@RequestBody HistorialRubroEntity historialRubro) {
        HistorialRubroEntity historialCreado = historialRubroService.save(historialRubro);
        return ResponseEntity.status(HttpStatus.CREATED).body(historialCreado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        historialRubroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}