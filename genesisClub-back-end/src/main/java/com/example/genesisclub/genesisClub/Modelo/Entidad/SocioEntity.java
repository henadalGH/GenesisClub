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
@Table(name = "socio", schema = "genesisclub")
public class SocioEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id_socio")
    private Long id;

    @Column(name = "cantidad_invitaciones")
    private Integer cantidadInvitaciones;

    @Column(name = "numero_postulaciones")
    private Integer numPostulaciones;

    @Column(name = "ultimo_movimieto")
    private LocalDate ultimoMovimiento;


    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoSocioEnitity estado;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "socioOrig")
    private List<InvitacionEntity> invitacion = new ArrayList<>();


    @OneToMany(mappedBy = "socio")
    private List<RelacionUsuarioEntity> relacion = new ArrayList<>();

    @OneToMany(mappedBy = "socio")
    private List<SolicitudEntity> solicitud = new ArrayList<>();

    @OneToMany(mappedBy = "socio")
    private List<RubroSocioEntity> socioRubro = new ArrayList<>();
}
