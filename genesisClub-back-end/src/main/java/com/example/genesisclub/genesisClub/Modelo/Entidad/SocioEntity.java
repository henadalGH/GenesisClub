package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "cantidad_invitaciones", nullable = false)
    private Integer cantidadInvitaciones = 0;

    @Column(name = "numero_postulaciones", nullable = false)
    private Integer numPostulaciones = 0;

    @Column(name = "ultimo_movimiento")
    private LocalDate ultimoMovimiento;

    // ================= RELACIONES =================

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties("socio")
    private EstadoSocioEnitity estado;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    // MULTINIVEL: Lista de personas a las que este socio invitó (Hijos/Contactos)
    @JsonIgnore
    @OneToMany(mappedBy = "socioPadre") 
    private List<RelacionUsuarioEntity> referidosDirectos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "socioOrigen")
    private List<InvitacionEntity> invitacion = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "socio")
    private List<SolicitudEntity> solicitud = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "socio")
    private List<RubroSocioEntity> socioRubro = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "socio")
    private List<SolicitudRubroEntity> solicitudesRubro = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (ultimoMovimiento == null) {
            ultimoMovimiento = LocalDate.now();
        }
    }
}