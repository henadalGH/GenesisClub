package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.AdminEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.JugadorEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RolEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UsuarioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.EstadoSocioEnitity;
import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Repositorio.AdminRepository;
import com.example.genesisclub.genesisClub.Repositorio.JugadorRepository;
import com.example.genesisclub.genesisClub.Repositorio.RolRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSocioRepository;
import com.example.genesisclub.genesisClub.Servicio.RegistroUsuarioServicio;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegistroUsuarioServicioImpl implements RegistroUsuarioServicio {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private JugadorRepository jugadorRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private SocioRepository socioRepository;
    @Autowired private EstadoSocioRepository estadoSocioRepository;
    @Autowired private PasswordEncoder encoder;

    // =============================
    // REGISTRO NORMAL (manual)
    // =============================
    @Override
    public ResponceDTO registrarUsuario(RegistroDTO dto) {
        return registrar(dto, false);
    }

    // =============================
    // DESDE SOLICITUD (aceptación)
    // =============================
    @Override
    public ResponceDTO registrarDesdeSolicitud(RegistroDTO dto) {
        return registrar(dto, true);
    }

    // =============================
    // MÉTODO CENTRAL
    // =============================
    private ResponceDTO registrar(RegistroDTO dto, boolean desdeSolicitud) {
        ResponceDTO response = new ResponceDTO();

        // 1. Validar Email
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            response.setNumOfErrors(1);
            response.setMensage("El email ya está registrado");
            return response;
        }

        // 2. Crear y persistir Usuario Base
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setFechaCreacion(LocalDate.now());

        if (desdeSolicitud) {
            usuario.setPassword(dto.getPassword());
        } else {
            usuario.setPassword(encoder.encode(dto.getPassword()));
        }

        // IMPORTANTE: Asegúrate que dto.getRol() coincida con el tipo de dato que espera findByNombre
        RolEntity rol = rolRepository
                .findByNombre(dto.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + dto.getRol()));

        usuario.setRol(rol);
        
        // Guardamos el usuario y obtenemos la entidad persistida (con ID)
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        // 3. CREAR ENTIDAD POR ROL (Revisar tipos de Enum aquí)
        // Usamos el nombre del rol para el switch si dto.getRol() es un String
        String nombreRol = rol.getNombre().name(); // Asumiendo que RolEntity.getNombre() devuelve un Enum

        switch (nombreRol) {
            case "ADMIN" -> {
                AdminEntity admin = new AdminEntity();
                admin.setUsuario(usuarioGuardado);
                adminRepository.save(admin);
            }
            case "JUGADOR" -> {
                JugadorEntity jugador = new JugadorEntity();
                jugador.setUsuario(usuarioGuardado);
                jugadorRepository.save(jugador);
            }
            case "SOCIO" -> {
                EstadoSocioEnitity estado = estadoSocioRepository
                        .findByEstado(dto.getEstado() != null ? dto.getEstado() : EstadoSocioEnums.ACTIVO)
                        .orElseThrow(() -> new RuntimeException("Estado de socio no encontrado"));

                SocioEntity socio = new SocioEntity();
                socio.setUsuario(usuarioGuardado);
                socio.setEstado(estado);
                socio.setCantidadInvitaciones(0);
                socio.setNumPostulaciones(0);
                socio.setUltimoMovimiento(LocalDate.now());
                socioRepository.save(socio);
            }
            default -> throw new RuntimeException("El rol " + nombreRol + " no tiene una entidad asociada definida en el switch.");
        }

        response.setMensage("Usuario creado correctamente con rol: " + nombreRol);
        return response;
    }
}