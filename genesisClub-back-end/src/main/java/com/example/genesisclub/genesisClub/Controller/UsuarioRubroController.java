package com.example.genesisclub.genesisClub.Controller;

import java.util.List;
import java.util.Optional;

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

import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioRubroEntity;
import com.example.genesisclub.genesisClub.Servicio.UsuarioRubroService;

@RestController
@RequestMapping("/api/usuario-rubro")
public class UsuarioRubroController {

    @Autowired
    private UsuarioRubroService usuarioRubroService;

    @GetMapping
    public ResponseEntity<List<UsuarioRubroEntity>> obtenerTodos() {
        List<UsuarioRubroEntity> usuarioRubros = usuarioRubroService.findAllUsuarioRubro();
        return ResponseEntity.ok(usuarioRubros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRubroEntity> obtenerPorId(@PathVariable Long id) {
        Optional<UsuarioRubroEntity> usuarioRubro = usuarioRubroService.findById(id);
        return usuarioRubro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioRubroEntity> crear(@RequestBody UsuarioRubroEntity usuarioRubro) {
        UsuarioRubroEntity usuarioRubroCreado = usuarioRubroService.save(usuarioRubro);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRubroCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRubroEntity> actualizar(@PathVariable Long id, @RequestBody UsuarioRubroEntity usuarioRubro) {
        Optional<UsuarioRubroEntity> usuarioRubroExistente = usuarioRubroService.findById(id);
        if (usuarioRubroExistente.isPresent()) {
            UsuarioRubroEntity actualizado = usuarioRubroExistente.get();
            actualizado.setActivo(usuarioRubro.isActivo());
            if (usuarioRubro.getFechaIngreso() != null) {
                actualizado.setFechaIngreso(usuarioRubro.getFechaIngreso());
            }
            if (usuarioRubro.getTipoUsuario() != null) {
                actualizado.setTipoUsuario(usuarioRubro.getTipoUsuario());
            }
            UsuarioRubroEntity usuarioRubroActualizado = usuarioRubroService.save(actualizado);
            return ResponseEntity.ok(usuarioRubroActualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioRubroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}