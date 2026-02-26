package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "invitacion", schema = "genesisclub")
public class InvitacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitacion")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "socio_origen_id", nullable = false)
    private SocioEntity socioOrigen;

    @Column(name = "email_destino", nullable = false)
    private String emailDestino;

    @Column(name = "fecha_envio")
    private LocalDate fechaEnvio;

    @Column(name = "fecha_repuesta")
    private LocalDate fechaRepusta;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;


    @ManyToOne
    @JoinColumn(name = "id_estado_invitacion")
    private EstadoInvitacionEntity estado;

    @ManyToOne
    @JoinColumn(name = "id_origen_socio")
    private SocioEntity socioOrig;

    @OneToMany(mappedBy = "invitacion")
    private List<SolicitudEntity> solicitud = new ArrayList<>();



}
