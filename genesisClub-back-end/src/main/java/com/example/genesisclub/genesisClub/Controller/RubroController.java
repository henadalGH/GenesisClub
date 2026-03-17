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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.RubroDTO;
import com.example.genesisclub.genesisClub.Servicio.RubroService;

@RestController
@RequestMapping("/api/rubro")
public class RubroController {

    @Autowired
    private RubroService rubroService;

    @GetMapping
    public ResponseEntity<List<RubroDTO>> obtenerTodos() {
        List<RubroDTO> rubros = rubroService.obtenerTodos();
        return ResponseEntity.ok(rubros);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<RubroDTO>> obtenerActivos() {
        List<RubroDTO> rubros = rubroService.obtenerActivos();
        return ResponseEntity.ok(rubros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RubroDTO> obtenerPorId(@PathVariable Long id) {
        try {
            RubroDTO rubro = rubroService.obtenerPorId(id);
            return ResponseEntity.ok(rubro);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RubroDTO> obtenerPorNombre(@PathVariable String nombre) {
        try {
            RubroDTO rubro = rubroService.obtenerPorNombre(nombre);
            return ResponseEntity.ok(rubro);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/clave/{clave}")
    public ResponseEntity<RubroDTO> obtenerPorClave(@PathVariable String clave) {
        try {
            RubroDTO rubro = rubroService.obtenerPorClaveAcceso(clave);
            return ResponseEntity.ok(rubro);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<RubroDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<RubroDTO> rubros = rubroService.buscarPorNombre(nombre);
        return ResponseEntity.ok(rubros);
    }

    @PostMapping
    public ResponseEntity<RubroDTO> crear(@RequestBody RubroDTO rubro) {
        RubroDTO rubroCreado = rubroService.crear(rubro);
        return ResponseEntity.status(HttpStatus.CREATED).body(rubroCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RubroDTO> actualizar(@PathVariable Long id, @RequestBody RubroDTO rubro) {
        try {
            RubroDTO rubroActualizado = rubroService.actualizar(id, rubro);
            return ResponseEntity.ok(rubroActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            rubroService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<RubroDTO> activar(@PathVariable Long id) {
        try {
            RubroDTO rubroActivado = rubroService.activar(id);
            return ResponseEntity.ok(rubroActivado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<RubroDTO> desactivar(@PathVariable Long id) {
        try {
            RubroDTO rubroDesactivado = rubroService.desactivar(id);
            return ResponseEntity.ok(rubroDesactivado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}