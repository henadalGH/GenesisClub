package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoinvitacionEnums;

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
@Table(name = "estadoInvitacion", schema = "genesisclub")
public class EstadoInvitacionEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_invitacion")
    private Long id;


    @Column(name = "nombre")
    @Enumerated(EnumType.STRING)
    private EstadoinvitacionEnums estado;

    @OneToMany(mappedBy = "estado")
    private List<InvitacionEntity> invitacion = new ArrayList<>();


}
