package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDateTime;

import com.example.genesisclub.genesisClub.Modelo.Enums.TipoUsuario;

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
@Table(name = "rubro_acceso_log", schema = "genesisclub")
public class RubroAccesoLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso_log")
    private Long id;

    @Column(name = "fecha_acceso")
    private LocalDateTime fechaAcceso;

    @Column(name = "clave_utilizada", length = 50)
    private String claveUtilizada;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario")
    private TipoUsuario tipoUsuario;

    @Column(name = "exitoso", columnDefinition = "boolean default true")
    private boolean exitoso = true;

    @Column(name = "ip_acceso", length = 45)
    private String ipAcceso;

    @ManyToOne
    @JoinColumn(name = "id_rubro")
    private RubroEntity rubro;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

}