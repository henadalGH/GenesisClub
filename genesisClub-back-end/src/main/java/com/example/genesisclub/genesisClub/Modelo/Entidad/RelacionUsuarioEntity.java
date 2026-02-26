package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.RelacionusuarioEnums;

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
@Table(name = "relacion_usuario", schema = "genesisclub")
public class RelacionUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relacaion")
    private Long id;

    @Column(name = "tipo_relacion")
    private RelacionusuarioEnums tipoRelacion;

    @Column(name = "fecha")
    private LocalDate fecha;


    @ManyToOne
    @JoinColumn(name = "id_socio")
    private SocioEntity socio;

    @ManyToOne
    @JoinColumn(name = "id_contacto")
    private UsuarioEntity contacto;

}
