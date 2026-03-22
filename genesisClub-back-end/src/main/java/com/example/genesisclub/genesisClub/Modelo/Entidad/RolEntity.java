package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;

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
@Table(name = "roles", schema = "genesisclub")
public class RolEntity {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;


    @Column(name = "nombre")
    @Enumerated(EnumType.STRING)
    private RolesEnums nombre;

    @JsonIgnore
    @OneToMany(mappedBy = "rol")
    private List<UsuarioEntity> usuario = new ArrayList<>();
    
}
