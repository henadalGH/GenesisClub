package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "relacion_usuario")
public class RelacionUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relacaion") // Nombre exacto de tu diagrama
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "tipo_relacion")
    private Integer nivel; // Usado para Nivel 1, 2, 3 en el multinivel

    // El Sponsor (quien invita) -> id_socio en tu DB
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_socio")
    private SocioEntity socioPadre;

    // El Referido (el invitado) -> id_contacto en tu DB
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contacto")
    private SocioEntity socioHijo;
}