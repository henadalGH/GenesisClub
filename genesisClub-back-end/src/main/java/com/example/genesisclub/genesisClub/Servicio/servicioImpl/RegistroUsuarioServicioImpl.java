package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;
import com.example.genesisclub.genesisClub.Modelo.Entidad.AdminEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.JugadorEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.RolEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.SocioEntity;
import com.example.genesisclub.genesisClub.Modelo.Entidad.UbicacionEntity;
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
import com.example.genesisclub.genesisClub.Servicio.UbicacionService;

@Service
@Transactional
public class RegistroUsuarioServicioImpl implements RegistroUsuarioServicio {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private UbicacionService ubicacionService;
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
    // MÉTODO CENTRAL DE REGISTRO
    // =============================
    private ResponceDTO registrar(RegistroDTO dto, boolean desdeSolicitud) {

        ResponceDTO response = new ResponceDTO();

        // 1️⃣ Validar email duplicado
        String emailNormalizado = dto.getEmail().toLowerCase().trim();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            response.setNumOfErrors(1);
            response.setMensage("El email ya está registrado");
            return response;
        }

        // 2️⃣ Crear usuario base
        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(emailNormalizado);
        usuario.setFechaCreacion(LocalDate.now());

        // completar teléfono automáticamente
        if (dto.getCodigoArea() != null) {
            usuario.setCodigoArea("0" + dto.getCodigoArea());
        }

        if (dto.getNumeroCelular() != null) {
            usuario.setNumeroCelular("15" + dto.getNumeroCelular());
        }

        // contraseña
        if (desdeSolicitud) {
            usuario.setPassword(dto.getPassword());
        } else {
            usuario.setPassword(encoder.encode(dto.getPassword()));
        }

        // 3️⃣ Obtener rol
        RolEntity rol = rolRepository
                .findByNombre(dto.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + dto.getRol()));

        usuario.setRol(rol);

        try {
            if (usuario.getCodigoArea() != null) {
                UbicacionEntity ubicacion = ubicacionService
                        .asignarUbicacionPorTelefono(
                                usuario.getCodigoArea(),
                                usuario.getNumeroCelular()
                        );

                usuario.setUbicacion(ubicacion);
            }
        } catch (Exception e) {
            // log opcional
            System.out.println("No se pudo asignar ubicación: " + e.getMessage());
        }
        // 4️⃣ Guardar usuario
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        // 5️⃣ Crear entidad según rol
        switch (rol.getNombre()) {

            case ADMIN -> {
                AdminEntity admin = new AdminEntity();
                admin.setUsuario(usuarioGuardado);
                adminRepository.save(admin);
            }

            case JUGADOR -> {
                JugadorEntity jugador = new JugadorEntity();
                jugador.setUsuario(usuarioGuardado);
                jugadorRepository.save(jugador);
            }

            case SOCIO -> {

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

            default -> throw new RuntimeException("El rol no tiene entidad asociada");
        }

        response.setMensage("Usuario creado correctamente con rol: " + rol.getNombre());

        return response;
    }
}