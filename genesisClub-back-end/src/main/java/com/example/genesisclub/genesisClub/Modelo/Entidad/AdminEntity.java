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
import lombok.Data;

@Entity
@Data
@Table(name = "administrador", schema = "genesisclub")
public class AdminEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "id_usuario") 
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "admin")
    private List<RubroEntity> rubros = new ArrayList<>();

    @OneToMany(mappedBy = "admin")
    private List<HistorialRubroEntity> historial = new ArrayList<>();

}
