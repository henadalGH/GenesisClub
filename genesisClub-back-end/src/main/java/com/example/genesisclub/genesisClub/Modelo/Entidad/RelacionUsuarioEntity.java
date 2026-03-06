package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.RelacionusuarioEnums;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "relacion_usuario")
public class RelacionUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relacion")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_relacion")
    private RelacionusuarioEnums tipoRelacion;

    @Column(name = "fecha")
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "id_socio")
    private SocioEntity socio;

    @ManyToOne
    @JoinColumn(name = "id_contacto")
    private SocioEntity contacto;
}