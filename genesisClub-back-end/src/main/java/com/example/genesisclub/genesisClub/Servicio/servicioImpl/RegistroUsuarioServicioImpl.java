package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;
import com.example.genesisclub.genesisClub.Repositorio.AdminRepository;
import com.example.genesisclub.genesisClub.Repositorio.JugadorRepository;
import com.example.genesisclub.genesisClub.Repositorio.RolRepository;
import com.example.genesisclub.genesisClub.Repositorio.SocioRepository;
import com.example.genesisclub.genesisClub.Repositorio.SolicitudReposistory;
import com.example.genesisclub.genesisClub.Repositorio.UsuarioRepository;
import com.example.genesisclub.genesisClub.Servicio.RegistroUsuarioServicio;
import com.example.genesisclub.genesisClub.Repositorio.EstadoSocioRepository;

import jakarta.transaction.Transactional;

@Service
public class RegistroUsuarioServicioImpl implements RegistroUsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private EstadoSocioRepository estadoSocioRepository;

    @Autowired
    private SolicitudReposistory solicitudRepository;

    
    @Override
    @Transactional
public ResponceDTO registrarUsuario(RegistroDTO usuarioDTO, RolesEnums rolEnum, EstadoSocioEnums estadoSocioEnum) throws Exception {
    ResponceDTO response = new ResponceDTO();

    // 1️⃣ VALIDACIÓN DE SEGURIDAD AMBIGUA
    // Chequeamos en la tabla de Usuarios Y en la tabla de Solicitudes
    boolean existeEnUsuarios = usuarioRepository.existsByEmail(usuarioDTO.getEmail());
    boolean existeEnSolicitudes = solicitudRepository.existsByEmail(usuarioDTO.getEmail());

    if (existeEnUsuarios || existeEnSolicitudes) {
        response.setNumOfErrors(1);
        // Mensaje idéntico para ambos casos para evitar rastreo de cuentas
        response.setMensage("El correo electrónico ya está vinculado a una cuenta o tiene una solicitud pendiente.");
        return response;
    }

    // 2️⃣ CREAR ENTIDAD USUARIO
    UsuarioEntity nuevoUsuario = new UsuarioEntity();
    nuevoUsuario.setNombre(usuarioDTO.getNombre());
    nuevoUsuario.setApellido(usuarioDTO.getApellido());
    nuevoUsuario.setEmail(usuarioDTO.getEmail());
    nuevoUsuario.setFechaCreacion(LocalDate.now());

    // 3️⃣ TRATAMIENTO DE PASSWORD (Evita doble encriptación)
    String passwordRecibida = usuarioDTO.getPassword();
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    if (passwordRecibida.startsWith("$2a$") || passwordRecibida.startsWith("$2b$") || passwordRecibida.startsWith("$2y$")) {
        nuevoUsuario.setPassword(passwordRecibida);
    } else {
        nuevoUsuario.setPassword(encoder.encode(passwordRecibida));
    }

    // 4️⃣ ASIGNAR ROL
    RolEntity rol = rolRepository.findByNombre(rolEnum)
            .orElseThrow(() -> new Exception("Rol no encontrado: " + rolEnum));
    nuevoUsuario.setRol(rol);

    // 5️⃣ GUARDAR USUARIO BASE
    usuarioRepository.save(nuevoUsuario);

    // 6️⃣ CREAR ENTIDAD ESPECÍFICA SEGÚN ROL
    switch (rolEnum) {
        case ADMIN:
            AdminEntity admin = new AdminEntity();
            admin.setUsuario(nuevoUsuario);
            adminRepository.save(admin);
            break;

        case JUGADOR:
            JugadorEntity jugador = new JugadorEntity();
            jugador.setUsuario(nuevoUsuario);
            jugadorRepository.save(jugador);
            break;

        case SOCIO:
            EstadoSocioEnums estadoEnum = (estadoSocioEnum != null) ? estadoSocioEnum : EstadoSocioEnums.ACTIVO;
            EstadoSocioEnitity estadoEntity = estadoSocioRepository
                    .findByEstado(estadoEnum)
                    .orElseThrow(() -> new Exception("Estado no encontrado: " + estadoEnum));

            SocioEntity socio = new SocioEntity();
            socio.setUsuario(nuevoUsuario);
            socio.setEstado(estadoEntity);
            socioRepository.save(socio);
            break;

        default:
            throw new Exception("Rol no soportado: " + rolEnum);
    }

    response.setMensage("Usuario registrado correctamente");
    return response;
}
}
