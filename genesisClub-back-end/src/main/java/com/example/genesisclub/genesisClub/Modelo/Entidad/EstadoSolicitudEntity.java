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
import lombok.Data;

@Entity
@Data
@Table(name = "estadoSolicitud", schema = "genesisclub")
public class EstadoSolicitudEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;


    @Column(name = "nombre")
    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEnums estado;

    @OneToMany(mappedBy = "estado")
    private List<SolicitudEntity> solicitud = new ArrayList<>();
}
