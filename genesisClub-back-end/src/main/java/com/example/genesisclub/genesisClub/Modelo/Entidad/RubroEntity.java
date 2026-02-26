package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
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
@Table(name = "rubros", schema = "genesisclub")
public class RubroEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rubro")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "activacion")
    private boolean activo;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacion;

    @ManyToOne
    @JoinColumn(name = "id_creador")
    private AdminEntity admin;

    @OneToMany(mappedBy = "rubro")
    private List<HistorialRubroEntity> historial = new ArrayList<>();

    @OneToMany(mappedBy = "rubro")
    private List<RubroSocioEntity> rubroSocio = new ArrayList<>();
}
