package com.example.genesisclub.genesisClub.Modelo.DTO.Responce;

import lombok.Data;

@Data
public class InvitacionResponseDTO {

    private Long id;
    private String emailDestino;
    private String token;
    private String estado;

}
