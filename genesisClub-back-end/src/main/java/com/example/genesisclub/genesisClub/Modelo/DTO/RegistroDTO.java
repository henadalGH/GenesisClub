package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;

public class RegistroDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private LocalDate fechaCreacion;
    private RolesEnums rol;

    private EstadoSocioEnums estado;
    
    public EstadoSocioEnums getEstado() {
        return estado;
    }
    public void setEstado(EstadoSocioEnums estado) {
        this.estado = estado;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public RolesEnums getRol() {
        return rol;
    }
    public void setRol(RolesEnums rol) {
        this.rol = rol;
    }


}
