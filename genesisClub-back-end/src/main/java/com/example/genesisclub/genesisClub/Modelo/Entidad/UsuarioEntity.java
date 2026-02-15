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

@Entity
@Data
@Table(name = "usuario", schema = "genesisclub", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "apellido")
    private String apellido;

    
    @Column(name = "nombre")
    private String nombre;

    

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private RolEntity rol;


    @OneToMany(mappedBy = "usuario")
    private List<SocioEntity> socio = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<AdminEntity> admin = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<JugadorEntity> jugador = new ArrayList<>();

    //Metodos de UserDetails

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
    public String getPassword()
    {
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
