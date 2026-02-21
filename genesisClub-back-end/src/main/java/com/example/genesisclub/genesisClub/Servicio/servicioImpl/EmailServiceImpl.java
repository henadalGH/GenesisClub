package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Servicio.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void enviarInvitacion(String destino, String link) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(destino);
        mail.setSubject("Invitación Genesis Club");
        mail.setText(
                "Fuiste invitado al sistema Genesis Club.\n\n" +
                "Registrate desde este enlace:\n" + link + "\n\n" +
                "Este link expira en 48 horas."
        );

        mailSender.send(mail);
    }
}