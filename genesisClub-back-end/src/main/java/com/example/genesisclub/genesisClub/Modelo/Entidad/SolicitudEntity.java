package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "solicitud", schema = "genesisclub")
public class SolicitudEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "email")
    private String email;

    @Column(name = "contacto")
    private String contacto;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoSolicitudEntity estado;


    @ManyToOne
    @JoinColumn(name = "id_invitacion")
    private InvitacionEntity invitacion;


}
