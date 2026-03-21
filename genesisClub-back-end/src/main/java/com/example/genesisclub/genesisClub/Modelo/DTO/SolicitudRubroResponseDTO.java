package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRubroResponseDTO {
    private Long id;
    private SocioInfoDTO socio;
    private RubroInfoDTO rubro;
    private String estado;
    private LocalDate fechaCreacion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocioInfoDTO {
        private Long id;
        private String nombreUsuario;
        private String emailUsuario;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RubroInfoDTO {
        private Long id;
        private String nombre;
        private String descripcion;
    }
}
