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
import lombok.Data;

@Entity
@Data
@Table(name = "rubro_socio", schema = "genesisclub")
public class RubroSocioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rubro_socio")
    private Long id;

    @Column(name = "fecha_asignacion")
    private LocalDate fechaAsignacion;

    @ManyToOne
    @JoinColumn(name = "id_socio")
    private SocioEntity socio;

    @ManyToOne
    @JoinColumn(name = "id_rubro")
    private RubroEntity rubro;

}
