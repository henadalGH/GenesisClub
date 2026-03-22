package com.example.genesisclub.genesisClub.Modelo.Entidad;

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
import jakarta.persistence.Version;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "administrador", schema = "genesisclub")
public class AdminEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;


    @ManyToOne
    @JoinColumn(name = "id_usuario") 
    private UsuarioEntity usuario;

    @JsonIgnore
    @OneToMany(mappedBy = "admin")
    private List<RubroEntity> rubros = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "admin")
    private List<HistorialRubroEntity> historial = new ArrayList<>();

}
