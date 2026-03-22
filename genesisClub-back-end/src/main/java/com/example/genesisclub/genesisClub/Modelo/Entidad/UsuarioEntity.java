package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Data
@Table(name = "usuario", schema = "genesisclub",uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    private String nombre;
    private String apellido;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    // ☎️ LO DEJAMOS COMO VOS QUERÉS
    @Column(name = "codigo_area")
    private String codigoArea;

    @Column(name = "celular")
    private String numeroCelular;

    // 📅 Fechas
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacion;

    @Column(name = "ultimo_login")
    private LocalDate ultimoLogin;

    // 🔐 Estado
    @Column(name = "verificacion_email")
    private boolean verificacionEmail;

    @Column(name = "estado")
    private String estado = "ACTIVO";

    // 🔗 Rol
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private RolEntity rol;

    // 📍 CLAVE → Ubicación del usuario
    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private UbicacionEntity ubicacion;

    // 🔁 Relaciones (las dejo como las tenías)
    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<SocioEntity> socio = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<AdminEntity> admin = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<JugadorEntity> jugador = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<PerfilEntity> perfil = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<NotificacionEntity> notificacion = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<VehiculoEntity> vehiculos = new ArrayList<>();

    // 🔐 UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (this.rol != null && this.rol.getNombre() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + this.rol.getNombre()));
        }
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

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}