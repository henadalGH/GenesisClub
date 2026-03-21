package com.example.genesisclub.genesisClub.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.RubroSocioDTO;
import com.example.genesisclub.genesisClub.Servicio.RubroSocioService;

@RestController
@RequestMapping("/api/rubro-socio")
public class RubroSocioController {

    @Autowired
    private RubroSocioService rubroSocioService;

    @GetMapping
    public ResponseEntity<List<RubroSocioDTO>> obtenerTodos() {
        List<RubroSocioDTO> rubroSocios = rubroSocioService.obtenerTodos();
        return ResponseEntity.ok(rubroSocios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RubroSocioDTO> obtenerPorId(@PathVariable Long id) {
        try {
            RubroSocioDTO rubroSocio = rubroSocioService.obtenerPorId(id);
            return ResponseEntity.ok(rubroSocio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/rubro/{idRubro}")
    public ResponseEntity<List<RubroSocioDTO>> obtenerPorRubro(@PathVariable Long idRubro) {
        List<RubroSocioDTO> rubroSocios = rubroSocioService.obtenerPorRubro(idRubro);
        return ResponseEntity.ok(rubroSocios);
    }

    @GetMapping("/socio/{idSocio}")
    public ResponseEntity<List<RubroSocioDTO>> obtenerPorSocio(@PathVariable Long idSocio) {
        List<RubroSocioDTO> rubroSocios = rubroSocioService.obtenerPorSocio(idSocio);
        return ResponseEntity.ok(rubroSocios);
    }

    @PostMapping
    public ResponseEntity<RubroSocioDTO> crear(@RequestBody RubroSocioDTO rubroSocio) {
        RubroSocioDTO rubroSocioCreado = rubroSocioService.crear(rubroSocio);
        return ResponseEntity.status(HttpStatus.CREATED).body(rubroSocioCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RubroSocioDTO> actualizar(@PathVariable Long id, @RequestBody RubroSocioDTO rubroSocio) {
        try {
            RubroSocioDTO rubroSocioActualizado = rubroSocioService.actualizar(id, rubroSocio);
            return ResponseEntity.ok(rubroSocioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            rubroSocioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/existe/rubro/{idRubro}/socio/{idSocio}")
    public ResponseEntity<Boolean> existeRelacion(@PathVariable Long idRubro, @PathVariable Long idSocio) {
        boolean existe = rubroSocioService.existeRelacion(idRubro, idSocio);
        return ResponseEntity.ok(existe);
    }

    @DeleteMapping("/rubro/{idRubro}/socio/{idSocio}")
    public ResponseEntity<Void> eliminarPorRubroYSocio(@PathVariable Long idRubro, @PathVariable Long idSocio) {
        rubroSocioService.eliminarRelacion(idRubro, idSocio);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/socio/{idSocio}/clave/{claveAcceso}")
    public ResponseEntity<RubroSocioDTO> asociarPorClave(@PathVariable Long idSocio, @PathVariable String claveAcceso) {
        try {
            RubroSocioDTO rubroSocio = rubroSocioService.asociarPorClaveAcceso(idSocio, claveAcceso);
            return ResponseEntity.status(HttpStatus.CREATED).body(rubroSocio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}