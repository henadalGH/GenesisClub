package com.example.genesisclub.genesisClub.Modelo.Entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "ubicacion", schema = "genesisclub")
public class UbicacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long id;

    // 🌎 Siempre Argentina (lo podés dejar fijo o eliminar más adelante)
    @Column(name = "pais", length = 100)
    private String pais = "Argentina";

    // 🧠 CLAVE para filtros
    @Column(name = "provincia", length = 100)
    private String provincia;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    // ☎️ Base de tu lógica actual
    @Column(name = "codigo_area", length = 10)
    private String codigoArea;

    // 🟡 NUEVO → para agrupar (Litoral, Centro, etc)
    @Column(name = "zona", length = 50)
    private String zona;

    // 📍 CLAVE para mapa (NO lo saques)
    @Column(name = "latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 11, scale = 8)
    private BigDecimal longitud;

    // 🏠 Opcional (para futuro)
    @Column(name = "direccion_completa", length = 255)
    private String direccionCompleta;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}