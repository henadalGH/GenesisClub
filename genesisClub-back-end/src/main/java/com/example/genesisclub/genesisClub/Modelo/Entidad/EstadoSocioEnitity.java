package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;

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
@Table(name = "estadoSocio", schema = "genesisclub")
public class EstadoSocioEnitity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long id;


    @Column(name = "nombre")
    @Enumerated(EnumType.STRING)
    private EstadoSocioEnums estado;

    @OneToMany(mappedBy = "estado")
    private List<SocioEntity> socio = new ArrayList<>();



}
