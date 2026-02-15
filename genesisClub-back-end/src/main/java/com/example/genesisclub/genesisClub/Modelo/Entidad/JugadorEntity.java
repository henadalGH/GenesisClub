package com.example.genesisclub.genesisClub.Modelo.Entidad;

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
@Table(name = "jugador", schema = "genesisclub")
public class JugadorEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_jugador")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "id_usuario") 
    private UsuarioEntity usuario;
}
