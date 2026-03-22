package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Entidad.enums.EstadoSolicitudRubro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Data
@Table(name = "solicitud_rubro", schema = "genesisclub")
public class SolicitudRubroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_rubro")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "id_socio")
    @JsonIgnoreProperties({"solicitudesRubro", "rubroSocio"})
    private SocioEntity socio;

    @ManyToOne
    @JoinColumn(name = "id_rubro")
    @JsonIgnoreProperties({"solicitudesRubro", "rubroSocio"})
    private RubroEntity rubro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSolicitudRubro estado;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

}