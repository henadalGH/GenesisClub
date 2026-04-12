package com.example.genesisclub.genesisClub.Modelo.DTO;

public class AdminResumenDTO {
    private long totalSocios;
    private long totalJugadores;
    private long solicitudesPendientes;
    private long solicitudesJugadoresPendientes;
    private long solicitudesRubrosPendientes;
    private long rubrosActivos;

    public AdminResumenDTO(long totalSocios, long totalJugadores, long solicitudesPendientes,
                           long solicitudesJugadoresPendientes, long solicitudesRubrosPendientes,
                           long rubrosActivos) {
        this.totalSocios = totalSocios;
        this.totalJugadores = totalJugadores;
        this.solicitudesPendientes = solicitudesPendientes;
        this.solicitudesJugadoresPendientes = solicitudesJugadoresPendientes;
        this.solicitudesRubrosPendientes = solicitudesRubrosPendientes;
        this.rubrosActivos = rubrosActivos;
    }

    // Getters
    public long getTotalSocios() {
        return totalSocios;
    }

    public long getTotalJugadores() {
        return totalJugadores;
    }

    public long getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public long getSolicitudesJugadoresPendientes() {
        return solicitudesJugadoresPendientes;
    }

    public long getSolicitudesRubrosPendientes() {
        return solicitudesRubrosPendientes;
    }

    public long getRubrosActivos() {
        return rubrosActivos;
    }
}