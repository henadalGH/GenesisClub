package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario", schema = "genesisclub",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacion;

    @Column(name = "ultimo_login")
    private LocalDate ultimoLogin;

    @Column(name = "verificacion_email")
    private boolean verificacionEmail;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private RolEntity rol;

    @OneToMany(mappedBy = "usuario")
    private List<SocioEntity> socios = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<AdminEntity> admins = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<JugadorEntity> jugadores = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<PerfilEntity> perfiles = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<NotificacionEntity> notificaciones = new ArrayList<>();

    // ================= UserDetails =================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.rol.getNombre()));

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}