package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.genesisclub.genesisClub.Modelo.DTO.EmailDTO;
import com.example.genesisclub.genesisClub.Servicio.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void enviarCorreo(EmailDTO emailDTO) { // Quitamos el throws para manejarlo aquí mismo

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailDTO.getDestinatario());
            helper.setSubject(emailDTO.getAsunto());

            // Procesar la plantilla HTML
            Context context = new Context();
            context.setVariable("mensaje", emailDTO.getMensaje());
            
            // "email" debe coincidir con el nombre de tu archivo en /templates/email.html
            String htmlContent = templateEngine.process("email", context);

            helper.setText(htmlContent, true);

            // Intentar enviar el correo
            javaMailSender.send(mimeMessage);
            System.out.println("Correo enviado con éxito a: " + emailDTO.getDestinatario());

        } catch (MessagingException e) {
            // Error específico de la estructura del correo (destinatario, asunto, etc.)
            System.err.println("Error al construir el mensaje de correo: " + e.getMessage());
            // Aquí podrías lanzar una excepción personalizada si lo necesitas
        } catch (Exception e) {
            // Error genérico (servidor caído, error en la plantilla Thymeleaf, etc.)
            System.err.println("Error inesperado al enviar el correo: " + e.getMessage());
        }
    }
}