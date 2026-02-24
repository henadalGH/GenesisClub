package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.NotificacionEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "notificaciones", schema = "genesisclub")
public class NotificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long id;

    @Column(name = "tipo_notificacion")
    @Enumerated(EnumType.STRING)
    private NotificacionEnums tipo;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "leida")
    private boolean leida;

    @Column(name = "fecha")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

}
