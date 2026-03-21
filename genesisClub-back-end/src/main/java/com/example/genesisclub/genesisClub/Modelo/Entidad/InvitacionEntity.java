package com.example.genesisclub.genesisClub.Modelo.Entidad;

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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "invitacion", schema = "genesisclub")
public class InvitacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitacion")
    private Long id;

    // 1. CORRECCIÓN CLAVE: El nombre debe ser 'id_socio_origen' para que coincida con tu DB
    @ManyToOne
    @JoinColumn(name = "id_socio_origen", nullable = false)
    private SocioEntity socioOrigen;

    @Column(name = "email_destino", nullable = false)
    private String emailDestino;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    // 2. REVISIÓN: Verifica que en tu DB la columna sea 'id_estado_invitacion'
    @ManyToOne
    @JoinColumn(name = "id_estado_invitacion")
    private EstadoInvitacionEntity estado;

    @JsonIgnore
    @OneToMany(mappedBy = "invitacion")
    private List<SolicitudEntity> solicitud = new ArrayList<>();

}