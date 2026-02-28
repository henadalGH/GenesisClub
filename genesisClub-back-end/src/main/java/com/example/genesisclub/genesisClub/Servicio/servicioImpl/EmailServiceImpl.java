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
    public void enviarCorreo(EmailDTO emailDTO) {
        try {
            // 1. Crear el mensaje MIME (permite HTML)
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            
            // true indica que es un mensaje multipart (necesario para HTML)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailDTO.getDestinatario());
            helper.setSubject(emailDTO.getAsunto());

            // 2. Preparar el contexto de Thymeleaf
            Context context = new Context();
            context.setVariable("mensaje", emailDTO.getMensaje());
            
            // 3. Procesar la plantilla
            // IMPORTANTE: Cambiado a "mail" porque tu archivo es mail.html
            String htmlContent = templateEngine.process("mail", context);

            // true indica que el texto es HTML
            helper.setText(htmlContent, true);

            // 4. Enviar
            javaMailSender.send(mimeMessage);
            
            System.out.println("✅ Correo enviado con éxito a: " + emailDTO.getDestinatario());

        } catch (MessagingException e) {
            System.err.println("❌ Error en la estructura del correo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error crítico al enviar correo: " + e.getClass().getSimpleName());
            System.err.println("Causa: " + e.getMessage());
            // Esto te mostrará en la consola si el archivo mail.html no existe
            e.printStackTrace(); 
        }
    }
}