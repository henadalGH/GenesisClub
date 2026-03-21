package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

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
            String htmlContent = templateEngine.process("mail", context);

            // true indica que el texto es HTML
            helper.setText(htmlContent, true);

            // 4. Enviar
            javaMailSender.send(mimeMessage);
            
            log.info("Correo enviado exitosamente a: {}", emailDTO.getDestinatario());

        } catch (MessagingException e) {
            log.error("Error en la estructura del correo para: {}", emailDTO.getDestinatario(), e);
        } catch (Exception e) {
            log.error("Error crítico al enviar correo a: {}", emailDTO.getDestinatario(), e);
        }
    }
}
