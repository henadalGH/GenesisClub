package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import com.example.genesisclub.genesisClub.Modelo.Enums.AccionRubroEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "historial_rubro", schema = "genesisclub")
public class HistorialRubroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial_rubro")
    private Long id;

    @Column(name = "accion")
    @Enumerated(EnumType.STRING)
    private AccionRubroEnums accion;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "fecha")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "id_rubro")
    private RubroEntity rubro;

    @ManyToOne
    @JoinColumn(name = "id_admin")
    private AdminEntity admin;
}
