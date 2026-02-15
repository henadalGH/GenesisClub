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

    @Override
    @Transactional
    public ResponceDTO registrarUsuario(RegistroDTO usuarioDTO, RolesEnums rolEnum, EstadoSocioEnums estadoSocioEnum) throws Exception {
        ResponceDTO response = new ResponceDTO();

        // Validación: email único
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            response.setNumOfErrors(1);
            response.setMensage("El email ya existe");
            return response;
        }

        // 1️⃣ Crear usuario
        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario.setNombre(usuarioDTO.getNombre());
        nuevoUsuario.setApellido(usuarioDTO.getApellido());
        nuevoUsuario.setEmail(usuarioDTO.getEmail());
        nuevoUsuario.setFechaCreacion(LocalDate.now());

        // Encriptar password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        nuevoUsuario.setPassword(encoder.encode(usuarioDTO.getPassword()));

        // 2️⃣ Asignar rol
        RolEntity rol = rolRepository.findByNombre(rolEnum)
                .orElseThrow(() -> new Exception("Rol no encontrado: " + rolEnum));
        nuevoUsuario.setRol(rol);

        // 3️⃣ Guardar usuario primero
        usuarioRepository.save(nuevoUsuario);

        // 4️⃣ Crear relación según el rol
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
                // Si no se pasa estado, usar ACTIVO por defecto
                EstadoSocioEnums estadoEnum = (estadoSocioEnum != null) ? estadoSocioEnum : EstadoSocioEnums.ACTIVO;

                // Buscar la entidad correspondiente
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
