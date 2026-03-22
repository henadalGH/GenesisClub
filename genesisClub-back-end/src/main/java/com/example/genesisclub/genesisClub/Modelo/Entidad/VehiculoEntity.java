package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "vehiculo", schema = "genesisclub")
public class VehiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "patente", length = 20, unique = true, nullable = false)
    private String patente;

    @Column(name = "marca", length = 100)
    private String marca; // nueva columna marca

    @Column(name = "tiene_gnc", columnDefinition = "boolean default false")
    private boolean tieneGnc = false;

    @Column(name = "modelo", length = 100)
    private String modelo;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "activo", columnDefinition = "boolean default true")
    private boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

}