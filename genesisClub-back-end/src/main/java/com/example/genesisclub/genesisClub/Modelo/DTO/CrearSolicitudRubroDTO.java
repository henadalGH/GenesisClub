package com.example.genesisclub.genesisClub.Modelo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearSolicitudRubroDTO {

    @NotNull(message = "socioId es obligatorio")
    private Long socioId;

    @NotNull(message = "rubroId es obligatorio")
    private Long rubroId;

    @NotBlank(message = "claveAcceso es obligatoria")
    private String claveAcceso;

    // Getters and setters
    public Long getSocioId() {
        return socioId;
    }

    public void setSocioId(Long socioId) {
        this.socioId = socioId;
    }

    public Long getRubroId() {
        return rubroId;
    }

    public void setRubroId(Long rubroId) {
        this.rubroId = rubroId;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

}