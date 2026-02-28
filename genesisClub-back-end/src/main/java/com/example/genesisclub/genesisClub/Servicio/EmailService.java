package com.example.genesisclub.genesisClub.Servicio;

import com.example.genesisclub.genesisClub.Modelo.DTO.EmailDTO;

import jakarta.mail.MessagingException;

public interface EmailService {

    public void enviarCorreo(EmailDTO emailDTO)throws MessagingException;

}
