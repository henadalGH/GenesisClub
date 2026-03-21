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
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column(name = "activo")
    private boolean activo;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacion;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private AdminEntity admin;

    @JsonIgnore
    @OneToMany(mappedBy = "rubro")
    private List<HistorialRubroEntity> historial = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "rubro")
    private List<RubroSocioEntity> rubroSocio = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "rubro")
    private List<SolicitudRubroEntity> solicitudesRubro = new ArrayList<>();

    @Column(name = "clave_acceso", length = 50, unique = true)
    private String claveAcceso;

    @Column(name = "fecha_clave_generacion")
    private LocalDate fechaClaveGeneracion;

    @Column(name = "clave_activa")
    private boolean claveActiva = true;

}