package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;

public class SolicitudDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String contacto;
    private LocalDate fechaSolicitud;
    private EstadoSolicitudEnums estado;
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
    public String getContacto() {
        return contacto;
    }
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }
    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
    public EstadoSolicitudEnums getEstado() {
        return estado;
    }
    public void setEstado(EstadoSolicitudEnums estado) {
        this.estado = estado;
    }

    
}
