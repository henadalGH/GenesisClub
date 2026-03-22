package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSolicitudEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "estadoSolicitud", schema = "genesisclub")
public class EstadoSolicitudEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;


    @Column(name = "nombre")
    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEnums estado;

    @JsonIgnore
    @OneToMany(mappedBy = "estado")
    private List<SolicitudEntity> solicitud = new ArrayList<>();
}
