package com.example.genesisclub.genesisClub.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genesisclub.genesisClub.Modelo.DTO.EmailDTO;
import com.example.genesisclub.genesisClub.Servicio.EmailService;

import jakarta.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/enviar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIO')")
    public ResponseEntity<String> enviarEmail(@RequestBody EmailDTO emailDTO) throws MessagingException {
        emailService.enviarCorreo(emailDTO);
        return new ResponseEntity<>("correo enviado", HttpStatus.OK);
    }
    
}
