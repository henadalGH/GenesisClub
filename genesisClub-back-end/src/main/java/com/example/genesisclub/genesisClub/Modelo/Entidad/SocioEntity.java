package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "socio")
public class SocioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_socio")
    private Long id;

    @Column(name = "cantidad_invitaciones", nullable = false)
    private Integer cantidadInvitaciones = 0;

    @Column(name = "numero_postulaciones", nullable = false)
    private Integer numPostulaciones = 0;

    @Column(name = "ultimo_movimiento")
    private LocalDate ultimoMovimiento;

    // ================= RELACIONES =================

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoSocioEnitity estado;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "socioOrigen")
    private List<InvitacionEntity> invitacion = new ArrayList<>();

    @OneToMany(mappedBy = "socio")
    private List<RelacionUsuarioEntity> relacion = new ArrayList<>();

    @OneToMany(mappedBy = "socio")
    private List<SolicitudEntity> solicitud = new ArrayList<>();

    @OneToMany(mappedBy = "socio")
    private List<RubroSocioEntity> socioRubro = new ArrayList<>();

    // ================= AUTO INIT =================

    @PrePersist
    public void prePersist() {
        if (ultimoMovimiento == null) {
            ultimoMovimiento = LocalDate.now();
        }
    }
}